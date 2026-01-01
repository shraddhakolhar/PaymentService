package com.scaler.paymentservice.repository;

import com.scaler.paymentservice.entity.Payment;
import com.scaler.paymentservice.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

     // Used during payment creation flows
    Optional<Payment> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<Payment> findByOrderId(Long orderId);

    // CRITICAL: Idempotent webhook lookup
    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

    /**
     * Prevents duplicate webhook race condition
     * Locks row during webhook processing
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p from Payment p
        where p.gatewayPaymentId = :gatewayPaymentId
        """)
    Optional<Payment> findByGatewayPaymentIdForUpdate(String gatewayPaymentId);

    // Optional safety checks
    boolean existsByGatewayPaymentId(String gatewayPaymentId);

    long countByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
