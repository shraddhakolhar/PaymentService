package com.scaler.paymentservice.adapters.paymentgatewaysadapters.stripe;

import com.scaler.paymentservice.adapters.paymentgatewaysadapters.PaymentGatewayAdapter;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("stripe")
public class StripePaymentGatewayAdapter implements PaymentGatewayAdapter {

    @Override
    public String createPaymentLink(Long price) {
        try {
            PaymentLinkCreateParams params =
                    PaymentLinkCreateParams.builder()
                            .addLineItem(
                                    PaymentLinkCreateParams.LineItem.builder()
                                            .setPrice("price_123") // test price id
                                            .setQuantity(1L)
                                            .build()
                            )
                            .build();

            PaymentLink paymentLink = PaymentLink.create(params);
            return paymentLink.getUrl();

        } catch (Exception e) {
            throw new RuntimeException("Stripe payment link creation failed", e);
        }
    }
}
