package com.scaler.paymentservice.models;

import com.scaler.paymentservice.models.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String paymentLink;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String gatewayReferenceId;
}
