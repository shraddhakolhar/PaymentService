package com.scaler.paymentservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePaymentLinkResponseDto {
    private String paymentUrl;
    private String paymentId;
}

