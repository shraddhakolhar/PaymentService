package com.scaler.paymentservice.gateway;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("stripe")
public class StripePaymentGateway implements PaymentGateway {

    @Value("${payment.success.url}")
    private String successUrl;

    @Value("${payment.cancel.url}")
    private String cancelUrl;

    public StripePaymentGateway(
            @Value("${stripe.secret.key}") String stripeSecretKey
    ) {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public CreatePaymentLinkResponseDto createPaymentLink(
            CreatePaymentLinkRequestDto request
    ) {
        try {
            long amountInPaise =
                    request.getAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .longValueExact();

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(successUrl)
                            .setCancelUrl(cancelUrl)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("inr")
                                                            .setUnitAmount(amountInPaise)
                                                            .setProductData(
                                                                    SessionCreateParams
                                                                            .LineItem
                                                                            .PriceData
                                                                            .ProductData
                                                                            .builder()
                                                                            .setName(
                                                                                    "Order #" + request.getOrderId()
                                                                            )
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            // Metadata = webhook truth
                            .putMetadata("orderId", request.getOrderId())
                            .build();

            Session session = Session.create(params);

            // CORRECT ORDER
            return new CreatePaymentLinkResponseDto(
                    session.getUrl(), // paymentUrl
                    session.getId()   // gatewayPaymentId
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Stripe payment link creation failed",
                    e
            );
        }
    }
}
