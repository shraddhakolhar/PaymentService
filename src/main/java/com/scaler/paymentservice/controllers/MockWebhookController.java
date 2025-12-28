package com.scaler.paymentservice.controllers;

import com.scaler.paymentservice.services.PaymentService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock/webhook")
@Profile("mock")
public class MockWebhookController {

    private final PaymentService paymentService;

    public MockWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/success/{referenceId}")
    public void markSuccess(@PathVariable String referenceId) {
        paymentService.markSuccess(referenceId);
    }

    @PostMapping("/failed/{referenceId}")
    public void markFailed(@PathVariable String referenceId) {
        paymentService.markFailed(referenceId);
    }
}
