package com.sign.infrastructure.repository;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryPasskeyRepositoryTest extends PasskeyRepositoryTest {

    private InMemoryPasskeyRepository inMemoryPasskeyRepository;

    @BeforeEach
    void setUp() {
        inMemoryPasskeyRepository = new InMemoryPasskeyRepository();
        passkeyRepository = inMemoryPasskeyRepository;
    }

    @Override
    public void cleanUp() {
        inMemoryPasskeyRepository = new InMemoryPasskeyRepository();
        passkeyRepository = inMemoryPasskeyRepository;
    }

    @Override
    public RegisteredCredential findRegisteredCredential(ByteArray credential, ByteArray userHandle) {
        return inMemoryPasskeyRepository.findByCredentialIdAndUserHandle(credential, userHandle);
    }
}
