package com.scaler.paymentservice.adapters.paymentgatewaysadapters.stripe;

import com.stripe.Stripe;
import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("stripe")   // 🔑 THIS FIXES THE ERROR
public class StripeConfiguration {

    @Value("${apikeys.stripe}")
    private String stripeApiKey;

    @Bean
    public StripeClient getStripeClient() {
        Stripe.apiKey = stripeApiKey;
        return new StripeClient(stripeApiKey);
    }
}
