package com.scaler.paymentservice.adapters.paymentgatewaysadapters;

import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayStrategy {

    private final PaymentGatewayAdapter paymentGatewayAdapter;

    public PaymentGatewayStrategy(PaymentGatewayAdapter paymentGatewayAdapter) {
        this.paymentGatewayAdapter = paymentGatewayAdapter;
    }

    public PaymentGatewayAdapter getPaymentGateway() {
        return paymentGatewayAdapter;
    }
}
