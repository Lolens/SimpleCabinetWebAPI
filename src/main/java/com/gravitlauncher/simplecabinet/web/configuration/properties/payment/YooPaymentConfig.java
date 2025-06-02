package com.gravitlauncher.simplecabinet.web.configuration.properties.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "payment.yoo")
public class YooPaymentConfig {
    public boolean enable;
    public String merchantId;
    public String secretKey;
    public String redirectUrl;
    public boolean test;

    public YooPaymentConfig() {
    }

}
