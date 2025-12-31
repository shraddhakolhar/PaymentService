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
     * ✅ Simulate payment success webhook (CORRECT)
     */
    @PostMapping("/payment-success")
    public ResponseEntity<Void> mockPaymentSuccess(
            @RequestBody Map<String, String> payload
    ) {

        String orderId = payload.get("orderId");
        String gatewayPaymentId = payload.get("gatewayPaymentId");

        // 1️⃣ Update payment DB
        paymentService.markPaymentSuccess(gatewayPaymentId);

        // 2️⃣ Notify Order Service
        restTemplate.postForEntity(
                orderServiceUrl + "/orders/" + orderId + "/paid",
                gatewayPaymentId,
                Void.class
        );

        return ResponseEntity.ok().build();
    }
}
