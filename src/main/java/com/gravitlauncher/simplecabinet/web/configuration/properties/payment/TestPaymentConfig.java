package com.gravitlauncher.simplecabinet.web.configuration.properties.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "payment.test")
public class TestPaymentConfig {
    public boolean enable;
    public String url;

    public TestPaymentConfig() {
    }


}
