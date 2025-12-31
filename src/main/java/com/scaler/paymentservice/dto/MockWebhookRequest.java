package com.scaler.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MockWebhookRequest {

    private Long orderId;
    private String paymentId;
}
