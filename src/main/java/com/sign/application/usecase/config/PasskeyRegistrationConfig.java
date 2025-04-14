package com.sign.application.usecase.config;

import com.sign.application.repository.HandleGenerator;
import com.sign.infrastructure.repository.HandleGeneratorImpl;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PasskeyRegistrationProperties.class)
@RequiredArgsConstructor
public class PasskeyRegistrationConfig {

    private final PasskeyRegistrationProperties passkeyRegistrationProperties;
    private final CredentialRepository credentialRepository;

    @Bean
    public RelyingParty relyingParty() {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(passkeyRegistrationProperties.id())
                .name(passkeyRegistrationProperties.name())
                .build();

        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialRepository)
                .origins(passkeyRegistrationProperties.origins())
                .build();
    }

    @Bean
    public HandleGenerator handleGenerator() {
        return new HandleGeneratorImpl(passkeyRegistrationProperties.handleLength());
    }
}
