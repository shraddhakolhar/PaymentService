package com.scaler.paymentservice.service;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;
import com.scaler.paymentservice.entity.Payment;
import com.scaler.paymentservice.entity.PaymentProvider;
import com.scaler.paymentservice.entity.PaymentStatus;
import com.scaler.paymentservice.gateway.StripePaymentGateway;
import com.scaler.paymentservice.repository.PaymentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("stripe")
@Service
public class StripePaymentService implements PaymentService {

    private final StripePaymentGateway stripePaymentGateway;
    private final PaymentRepository paymentRepository;

    public StripePaymentService(
            StripePaymentGateway stripePaymentGateway,
            PaymentRepository paymentRepository
    ) {
        this.stripePaymentGateway = stripePaymentGateway;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public CreatePaymentLinkResponseDto createPayment(
            CreatePaymentLinkRequestDto request,
            String userEmail
    ) {

        // 1️⃣ Call Stripe
        CreatePaymentLinkResponseDto response =
                stripePaymentGateway.createPaymentLink(request);

        // 2️⃣ Persist payment
        Payment payment = Payment.builder()
                .orderId(Long.valueOf(request.getOrderId()))
                .userEmail(userEmail)
                .amount(request.getAmount())
                .paymentGateway(PaymentProvider.STRIPE)
                .paymentLink(response.getPaymentUrl())
                .gatewayPaymentId(response.getPaymentId())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        return response;
    }

    @Override
    @Transactional
    public void markPaymentSuccess(String gatewayPaymentId) {
        Payment payment = paymentRepository
                .findByGatewayPaymentId(gatewayPaymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found: " + gatewayPaymentId)
                );

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }
}
