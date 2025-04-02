package com.sign.support.fixture;

import com.sign.application.repository.PasskeyRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import java.util.Set;

public class RelyingPartyFixture {

    public static RelyingParty create(PasskeyRepository passkeyRepository) {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("sign.co.kr")
                .name("sign test")
                .build();

        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(passkeyRepository)
                .origins(Set.of("https://sign.co.kr"))
                .build();
    }
}
