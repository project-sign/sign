package com.sign.support.fixture;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import java.util.Set;

public class RelyingPartyFixture {

    public static RelyingParty create(CredentialRepository credentialRepository) {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("sign.co.kr")
                .name("sign test")
                .build();

        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialRepository)
                .origins(Set.of("https://sign.co.kr"))
                .build();
    }
}
