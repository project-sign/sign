package com.sign.infrastructure.repository;

import com.sign.infrastructure.jpa.PasskeyEntity;
import com.sign.infrastructure.jpa.repository.PasskeyJpaRepository;
import com.sign.infrastructure.jpa.repository.RegisteredCredentialJpaRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CredentialRepositoryImpl implements CredentialRepository {

    private final PasskeyJpaRepository passkeyJpaRepository;
    private final RegisteredCredentialJpaRepository registeredCredentialJpaRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String email) {
        return passkeyJpaRepository.findByEmail(email)
                .map(passkey -> registeredCredentialJpaRepository.findByPasskey(passkey).stream()
                        .map(it -> PublicKeyCredentialDescriptor.builder()
                                .id(it.getCredentialId())
                                .build()))
                .orElseGet(Stream::empty)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String email) {
        return passkeyJpaRepository.findByEmail(email)
                .map(PasskeyEntity::getUserHandle);
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return passkeyJpaRepository.findByUserHandle(userHandle)
                .map(PasskeyEntity::getEmail);
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return registeredCredentialJpaRepository.findByCredentialIdAndUserHandle(credentialId, userHandle)
                .map(it -> RegisteredCredential.builder()
                        .credentialId(it.getCredentialId())
                        .userHandle(it.getUserHandle())
                        .publicKeyCose(it.getPublicKeyCose())
                        .signatureCount(it.getSignatureCount())
                        .build());
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return registeredCredentialJpaRepository.findByCredentialId(credentialId).stream()
                .map(it -> RegisteredCredential.builder()
                        .credentialId(it.getCredentialId())
                        .userHandle(it.getUserHandle())
                        .publicKeyCose(it.getPublicKeyCose())
                        .build())
                .collect(Collectors.toSet());
    }
}
