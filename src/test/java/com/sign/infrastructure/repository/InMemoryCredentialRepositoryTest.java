package com.sign.infrastructure.repository;

import com.yubico.webauthn.RegisteredCredential;

public class InMemoryCredentialRepositoryTest extends CredentialRepositoryTest {

    private InMemoryCredentialRepository inMemoryCredentialRepository = new InMemoryCredentialRepository();

    @Override
    public void cleanUp() {
        inMemoryCredentialRepository = new InMemoryCredentialRepository();
        credentialRepository = inMemoryCredentialRepository;
    }

    @Override
    public void saveData(String email, RegisteredCredential registeredCredential) {
        inMemoryCredentialRepository.save(email, registeredCredential);
    }
}
