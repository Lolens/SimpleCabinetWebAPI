package com.gravitlauncher.simplecabinet.web.configuration.properties.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "payment.stripe")
public class StripePaymentConfig {
    public boolean enable;
    public String apiKey;
    public String priceId;
    public String webhookSecret;
    public String redirectUrl;
    public boolean test;

    public StripePaymentConfig() {
    }

}
