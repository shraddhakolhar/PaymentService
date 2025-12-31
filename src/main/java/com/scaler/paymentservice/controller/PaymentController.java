package com.scaler.paymentservice.controller;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;
import com.scaler.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-link")
    public ResponseEntity<CreatePaymentLinkResponseDto> createPaymentLink(
            @Valid @RequestBody CreatePaymentLinkRequestDto request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                paymentService.createPayment(
                        request,
                        authentication.getName()
                )
        );
    }
}
