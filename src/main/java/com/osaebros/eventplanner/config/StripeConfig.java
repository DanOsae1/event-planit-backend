package com.osaebros.eventplanner.config;

import com.stripe.Stripe;
import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.key}")
    private String stripeApiKey;

    @Bean
    public StripeClient stripeClient() {
        Stripe.apiKey = stripeApiKey;

        return StripeClient.builder()
                .setApiKey(stripeApiKey)
                .setMaxNetworkRetries(2)
                .setConnectTimeout(30 * 1000)
                .setReadTimeout(80 * 1000)
                .build();
    }
}
