package com.scaler.paymentservice.controller;

import com.scaler.paymentservice.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@Profile("stripe")
@RequestMapping("/webhooks/stripe")
public class StripeWebhookController {

    private final RestTemplate restTemplate;
    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public StripeWebhookController(
            RestTemplate restTemplate,
            PaymentService paymentService
    ) {
        this.restTemplate = restTemplate;
        this.paymentService = paymentService;
    }

    /**
     * IDEMPOTENT STRIPE WEBHOOK
     */
    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {

        try {
            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    webhookSecret
            );

            if ("checkout.session.completed".equals(event.getType())) {

                Session session = (Session) event
                        .getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String gatewayPaymentId = session.getId();
                String orderId = session.getMetadata().get("orderId");

                // Update payment DB (IDEMPOTENT)
                boolean processed =
                        paymentService.markPaymentSuccess(gatewayPaymentId);

                // Notify OrderService ONLY ONCE
                if (processed) {
                    restTemplate.postForEntity(
                            orderServiceUrl + "/orders/" + orderId + "/paid",
                            gatewayPaymentId,
                            Void.class
                    );
                }
            }

            // Always return 200 to Stripe
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            // DO NOT return non-2xx → Stripe will retry forever
            e.printStackTrace();
            return ResponseEntity.ok().build();
        }
    }
}
