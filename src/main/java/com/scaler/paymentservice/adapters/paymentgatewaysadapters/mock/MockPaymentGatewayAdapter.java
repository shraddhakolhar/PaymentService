package com.scaler.paymentservice.adapters.paymentgatewaysadapters.mock;

import com.scaler.paymentservice.adapters.paymentgatewaysadapters.PaymentGatewayAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("mock")   // 🔑 VERY IMPORTANT
public class MockPaymentGatewayAdapter implements PaymentGatewayAdapter {

    @Override
    public String createPaymentLink(Long price) {
        // Fake but realistic payment link
        return "http://mock-payment-gateway/pay/" + UUID.randomUUID();
    }
}
