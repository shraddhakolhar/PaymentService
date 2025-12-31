package com.scaler.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePaymentLinkRequestDto {

    @NotNull
    private String orderId;

    @NotNull
    private BigDecimal amount;
}
