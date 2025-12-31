package com.scaler.paymentservice.gateway;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;

public interface PaymentGateway {

    CreatePaymentLinkResponseDto createPaymentLink(
            CreatePaymentLinkRequestDto request
    );
}
