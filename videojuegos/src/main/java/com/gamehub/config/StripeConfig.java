package com.gamehub.config;

import com.stripe.Stripe;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class StripeConfig {

    @Value("${stripe.api.key.public}")
    private String publicKey;

    @Value("${stripe.api.key.secret}")
    private String secretKey;

    @Value("${stripe.api.key.webhook}")
    private String webhookSecret;

    @Value("${stripe.currency}")
    private String currency;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Bean
    public String stripePublicKey() {
        return publicKey;
    }

    @Bean
    public String stripeSecretKey() {
        return secretKey;
    }

    @Bean
    public String stripeWebhookSecret() {
        return webhookSecret;
    }

    @Bean
    public String stripeCurrency() {
        return currency;
    }
}
