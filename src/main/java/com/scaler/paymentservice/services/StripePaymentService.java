package com.scaler.paymentservice.services;

import com.scaler.paymentservice.adapters.paymentgatewaysadapters.PaymentGatewayStrategy;
import com.scaler.paymentservice.models.Payment;
import com.scaler.paymentservice.models.PaymentStatus;
import com.scaler.paymentservice.repositories.PaymentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Profile("stripe")
public class StripePaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayStrategy strategy;

    public StripePaymentService(PaymentRepository paymentRepository,
                                PaymentGatewayStrategy strategy) {
        this.paymentRepository = paymentRepository;
        this.strategy = strategy;
    }

    @Override
    @Transactional
    public String createPaymentLink(Long orderId) {

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setStatus(PaymentStatus.CREATED);

        String gatewayReferenceId = UUID.randomUUID().toString();
        payment.setGatewayReferenceId(gatewayReferenceId);

        paymentRepository.save(payment);

        String link = strategy.getPaymentGateway()
                .createPaymentLink(orderId);

        payment.setPaymentLink(link);
        payment.setStatus(PaymentStatus.PAYMENT_LINK_CREATED);

        paymentRepository.save(payment);

        return link;
    }

    @Override
    @Transactional
    public void markSuccess(String gatewayReferenceId) {
        Payment payment =
                paymentRepository.findByPaymentGatewayReferenceId(gatewayReferenceId);

        if (payment == null) {
            throw new IllegalStateException("Payment not found: " + gatewayReferenceId);
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void markFailed(String gatewayReferenceId) {
        Payment payment =
                paymentRepository.findByPaymentGatewayReferenceId(gatewayReferenceId);

        if (payment == null) {
            throw new IllegalStateException("Payment not found: " + gatewayReferenceId);
        }

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }
}
