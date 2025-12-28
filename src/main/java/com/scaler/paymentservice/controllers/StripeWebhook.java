package com.scaler.paymentservice.controllers;

import com.scaler.paymentservice.services.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/webhook")
@Profile("stripe")
public class StripeWebhook {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public StripeWebhook(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader
    ) {

        Event event;
        try {
            event = Webhook.constructEvent(
                    payload,
                    signatureHeader,
                    webhookSecret
            );
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().build();
        }

        switch (event.getType()) {

            case "payment_link.completed" -> {
                String gatewayReferenceId = event.getId();
                paymentService.markSuccess(gatewayReferenceId);
            }

            case "payment_intent.payment_failed" -> {
                String gatewayReferenceId = event.getId();
                paymentService.markFailed(gatewayReferenceId);
            }

            default -> {
                // ignore other events
            }
        }

        return ResponseEntity.ok().build();
    }
}
