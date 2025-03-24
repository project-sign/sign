package com.sign.application.usecase.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EmailCertificationProperties.class)
public class EmailCertificationConfig {
}
