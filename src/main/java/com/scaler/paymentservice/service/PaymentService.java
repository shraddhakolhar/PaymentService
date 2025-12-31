package com.scaler.paymentservice.service;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;

public interface PaymentService {

    CreatePaymentLinkResponseDto createPayment(
            CreatePaymentLinkRequestDto request,
            String userEmail
    );

    void markPaymentSuccess(String gatewayPaymentId);
}
