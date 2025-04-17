package com.sign.infrastructure.repository;

import com.sign.application.repository.PasskeyRepository;
import com.sign.infrastructure.jpa.PasskeyEntity;
import com.sign.infrastructure.jpa.RegisteredCredentialEntity;
import com.sign.infrastructure.jpa.repository.PasskeyJpaRepository;
import com.sign.infrastructure.jpa.repository.RegisteredCredentialJpaRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class PasskeyRepositoryImpl implements PasskeyRepository {

    private final PasskeyJpaRepository passkeyJpaRepository;
    private final RegisteredCredentialJpaRepository registeredCredentialJpaRepository;

    @Override
    public Optional<ByteArray> findUserHandleByEmail(String email) {
        return passkeyJpaRepository.findByEmail(email)
                .map(PasskeyEntity::getUserHandle);
    }

    @Override
    @Transactional
    public void save(String email, RegisteredCredential credential) {
        PasskeyEntity passkey = passkeyJpaRepository.findByEmail(email)
                .orElseGet(() -> PasskeyEntity.builder()
                        .email(email)
                        .userHandle(credential.getUserHandle())
                        .build());
        PasskeyEntity savedPasskey = passkeyJpaRepository.save(passkey);
        RegisteredCredentialEntity newCredential = RegisteredCredentialEntity.builder()
                .credentialId(credential.getCredentialId())
                .userHandle(passkey.getUserHandle())
                .publicKeyCose(credential.getPublicKeyCose())
                .signatureCount(credential.getSignatureCount())
                .passkey(savedPasskey)
                .build();
        registeredCredentialJpaRepository.save(newCredential);
    }

    @Override
    @Transactional
    public void updateSignatureCount(String email, ByteArray credentialId, long newSignatureCount) {
        //TODO Lock 걸어야할까?
        RegisteredCredentialEntity registeredCredential = registeredCredentialJpaRepository.findByCredentialId(
                credentialId).orElseGet(() -> RegisteredCredentialEntity.builder()
                .credentialId(credentialId)
                .build());
        registeredCredential.updateSignatureCount(newSignatureCount);
    }
}
