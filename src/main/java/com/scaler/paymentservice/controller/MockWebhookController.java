package com.scaler.paymentservice.controller;

import com.scaler.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Profile("mock")
@RestController
@RequestMapping("/mock/webhook")
public class MockWebhookController {

    private final RestTemplate restTemplate;
    private final PaymentService paymentService;

    /**
     * MUST match application.properties
     * orders.service.url=http://localhost:8483
     */
    @Value("${order.service.url}")
    private String orderServiceUrl;

    public MockWebhookController(
            RestTemplate restTemplate,
            PaymentService paymentService
    ) {
        this.restTemplate = restTemplate;
        this.paymentService = paymentService;
    }

    /**
     * IDEMPOTENT MOCK WEBHOOK
     *
     * Payload example:
     * {
     *   "orderId": "1",
     *   "gatewayPaymentId": "mock-pay-xxxx"
     * }
     */
    @PostMapping("/payment-success")
    public ResponseEntity<Void> mockPaymentSuccess(
            @RequestBody Map<String, String> payload
    ) {

        String orderId = payload.get("orderId");
        String gatewayPaymentId = payload.get("gatewayPaymentId");

        // Update payment DB (idempotent)
        boolean stateChanged =
                paymentService.markPaymentSuccess(gatewayPaymentId);

        // Notify Order Service ONLY once
        if (stateChanged) {
            restTemplate.postForEntity(
                    orderServiceUrl + "/orders/" + orderId + "/paid",
                    gatewayPaymentId,
                    Void.class
            );
        }

        return ResponseEntity.ok().build();
    }
}
