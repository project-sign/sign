package com.sign.controller.config;

import com.sign.domain.SecretKeyValidator;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "protected-value")
public record ResponseProtectorProperties(String secretKey, String footer, Duration expired) {
    public ResponseProtectorProperties {
        SecretKeyValidator.validate(secretKey);
    }
}
