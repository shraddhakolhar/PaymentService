package com.scaler.paymentservice.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public void markOrderPaid(String orderId, String paymentId) {
        restTemplate.postForEntity(
                "http://localhost:8083/orders/" + orderId + "/paid",
                paymentId,
                Void.class
        );
    }
}
