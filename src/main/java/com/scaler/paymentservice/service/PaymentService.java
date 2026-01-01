package com.scaler.paymentservice.service;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;

public interface PaymentService {

    CreatePaymentLinkResponseDto createPayment(
            CreatePaymentLinkRequestDto request,
            String userEmail
    );

    /**
     * @return true if payment transitioned to SUCCESS now
     */
    boolean markPaymentSuccess(String gatewayPaymentId);
}
