package com.scaler.paymentservice.gateway;

import com.scaler.paymentservice.dto.CreatePaymentLinkRequestDto;
import com.scaler.paymentservice.dto.CreatePaymentLinkResponseDto;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
                                                            // rupees → paise
                                                            .setUnitAmount(
                                                                    request.getAmount().longValue() * 100
                                                            )
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData
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
                            .putMetadata("orderId", request.getOrderId())
                            .build();

            Session session = Session.create(params);

            return new CreatePaymentLinkResponseDto(
                    session.getId(),   // paymentId
                    session.getUrl()   // paymentUrl
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Stripe payment link creation failed",
                    e
            );
        }
    }
}
