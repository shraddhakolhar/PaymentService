package com.scaler.paymentservice.service;

import com.stripe.model.Event;

public interface PaymentWebhookService {
    void handleEvent(Event event);
}
