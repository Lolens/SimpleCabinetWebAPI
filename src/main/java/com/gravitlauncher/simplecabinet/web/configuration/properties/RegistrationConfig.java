package com.gravitlauncher.simplecabinet.web.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "registration")
public class RegistrationConfig {
    private boolean enabled;
    private RegistrationMode mode;
    private int minPasswordLength;
    private int maxPasswordLength;
    private String confirmUrl;

    public RegistrationConfig() {
    }

    public enum RegistrationMode {
        DEFAULT, ADMIN_ACCEPT, EMAIL_CONFIRM
    }
}
