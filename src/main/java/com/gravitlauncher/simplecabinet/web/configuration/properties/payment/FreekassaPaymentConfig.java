package com.gravitlauncher.simplecabinet.web.configuration.properties.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "payment.freekassa")
public class FreekassaPaymentConfig {
    public boolean enable;
    public String shopId;
    public String apiKey;
    public String paymentSystemId;
    public String secret;
    public boolean test;

    public FreekassaPaymentConfig() {
    }


}
