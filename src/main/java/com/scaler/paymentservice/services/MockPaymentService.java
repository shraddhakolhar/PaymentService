package com.scaler.paymentservice.services;

import com.scaler.paymentservice.adapters.paymentgatewaysadapters.PaymentGatewayStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class MockPaymentService implements PaymentService {

    private final PaymentGatewayStrategy strategy;

    public MockPaymentService(PaymentGatewayStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public String createPaymentLink(Long orderId) {
        return strategy.getPaymentGateway()
                .createPaymentLink(orderId);
    }

    @Override
    public void markSuccess(String gatewayReferenceId) {
        System.out.println("MOCK SUCCESS: " + gatewayReferenceId);
    }

    @Override
    public void markFailed(String gatewayReferenceId) {
        System.out.println("MOCK FAILED: " + gatewayReferenceId);
    }
}
