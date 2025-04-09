package com.sign.controller.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "protected-value")
public record ResponseProtectorProperties(String plainPassword, String footer, Duration expired) {
}
