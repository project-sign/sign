package com.sign.application.usecase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email-certification")
public record EmailCertificationProperties(
        int expiredTimeAsSeconds,
        int reSendTimeAsSeconds,
        String mailHost
) {
}
