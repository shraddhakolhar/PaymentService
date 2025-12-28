package com.scaler.paymentservice.services;

public interface PaymentService {

    String createPaymentLink(Long orderId);

    void markSuccess(String gatewayReferenceId);

    void markFailed(String gatewayReferenceId);
}

