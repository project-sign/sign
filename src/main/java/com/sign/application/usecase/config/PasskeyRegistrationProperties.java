package com.sign.application.usecase.config;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "passkey.relying-party")
public record PasskeyRegistrationProperties(
        String id,
        String name,
        Set<String> origins,
        Integer handleLength
) {
}
