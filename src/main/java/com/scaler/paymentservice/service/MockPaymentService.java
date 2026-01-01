package com.scaler.paymentservice.service;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;
import com.scaler.paymentservice.entity.Payment;
import com.scaler.paymentservice.entity.PaymentProvider;
import com.scaler.paymentservice.entity.PaymentStatus;
import com.scaler.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Profile("mock")
@Service
public class MockPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${mock.payment.url}")
    private String mockPaymentUrl;

    public MockPaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * ✅ Create payment (normal flow)
     */
    @Override
    @Transactional
    public CreatePaymentLinkResponseDto createPayment(
            CreatePaymentLinkRequestDto request,
            String userEmail
    ) {

        String gatewayPaymentId = "mock-pay-" + UUID.randomUUID();
        String paymentLink = mockPaymentUrl + gatewayPaymentId;

        Payment payment = Payment.builder()
                .orderId(Long.valueOf(request.getOrderId()))
                .userEmail(userEmail)
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentGateway(PaymentProvider.MOCK)
                .gatewayPaymentId(gatewayPaymentId)
                .paymentLink(paymentLink)
                .build(); // timestamps handled by @PrePersist

        paymentRepository.save(payment);

        return new CreatePaymentLinkResponseDto(
                paymentLink,
                gatewayPaymentId
        );
    }

    /**
     * 🔐 IDEMPOTENT + THREAD SAFE WEBHOOK
     *
     * @return true  -> payment status changed NOW
     * @return false -> webhook already processed earlier
     */
    @Override
    @Transactional
    public boolean markPaymentSuccess(String gatewayPaymentId) {

        Payment payment = paymentRepository
                .findByGatewayPaymentIdForUpdate(gatewayPaymentId) // 🔒 LOCK ROW
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found for gatewayPaymentId: " + gatewayPaymentId
                        )
                );

        // 🔁 IDEMPOTENCY GUARD
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return false; // already processed → safe no-op
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        // updatedAt handled by @PreUpdate

        paymentRepository.save(payment);

        return true; // FIRST time success
    }
}
