package com.scaler.paymentservice.repository;

import com.scaler.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

}
