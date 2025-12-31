package com.scaler.paymentservice.service;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;
import com.scaler.paymentservice.entity.Payment;
import com.scaler.paymentservice.entity.PaymentProvider;
import com.scaler.paymentservice.entity.PaymentStatus;
import com.scaler.paymentservice.repository.PaymentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Profile("mock")
@Service
public class MockPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    public MockPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public CreatePaymentLinkResponseDto createPayment(
            CreatePaymentLinkRequestDto request,
            String userEmail
    ) {

        String paymentId = "mock-pay-" + UUID.randomUUID();
        String paymentLink = "http://localhost:8082/mock/pay/" + paymentId;

        // ✅ Persist payment
        Payment payment = Payment.builder()
                .orderId(Long.valueOf(request.getOrderId()))
                .userEmail(userEmail)
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentGateway(PaymentProvider.MOCK)
                .gatewayPaymentId(paymentId)
                .paymentLink(paymentLink)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return new CreatePaymentLinkResponseDto(
                paymentLink,
                paymentId
        );
    }

    @Transactional
    @Override
    public void markPaymentSuccess(String gatewayPaymentId) {

        Payment payment = paymentRepository
                .findByGatewayPaymentId(gatewayPaymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found for gatewayPaymentId: " + gatewayPaymentId)
                );

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }
}
