package com.scaler.paymentservice.gateway;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mock")
public class MockPaymentGateway implements PaymentGateway {

    @Value("${mock.payment.url}")
    private String mockPaymentUrl;

    @Override
    public CreatePaymentLinkResponseDto createPaymentLink(
            CreatePaymentLinkRequestDto request
    ) {
        return CreatePaymentLinkResponseDto.builder()
                .paymentId("mock_payment_" + System.currentTimeMillis())
                .paymentUrl(mockPaymentUrl)   // ✅ FIXED
                .build();
    }
}
