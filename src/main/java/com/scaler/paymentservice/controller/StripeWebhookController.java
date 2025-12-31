package com.scaler.paymentservice.controller;

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

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public StripeWebhookController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws Exception {

        Event event = Webhook.constructEvent(
                payload,
                sigHeader,
                webhookSecret
        );

        if ("checkout.session.completed".equals(event.getType())) {

            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow();

            Long orderId = Long.valueOf(
                    session.getMetadata().get("orderId")
            );

            restTemplate.postForEntity(
                    orderServiceUrl + "/orders/" + orderId + "/paid",
                    session.getId(),
                    Void.class
            );
        }

        return ResponseEntity.ok().build();
    }
}
