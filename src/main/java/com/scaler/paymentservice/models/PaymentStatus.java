package com.scaler.paymentservice.models;

public enum PaymentStatus {

    CREATED,                // Payment object created in DB
    PAYMENT_LINK_CREATED,   // Gateway link generated
    SUCCESS,                // Payment completed
    FAILED,                 // Payment failed
    CANCELLED               // User cancelled / expired
}
