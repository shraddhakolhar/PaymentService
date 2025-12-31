package com.scaler.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Order for which this payment is created
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * User who is paying
     */
    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider paymentGateway;

    /**
     * Payment gateway reference / transaction id
     */
    private String gatewayPaymentId;

    private String paymentLink;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
