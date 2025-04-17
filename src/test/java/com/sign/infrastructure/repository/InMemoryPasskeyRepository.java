package com.sign.infrastructure.repository;

import com.sign.application.repository.PasskeyRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryPasskeyRepository implements PasskeyRepository {

    private final Map<String, ByteArray> handlerMapper;
    private final Map<ByteArray, List<RegisteredCredential>> credentialMapper;

    public InMemoryPasskeyRepository(Map<String, ByteArray> handlerMapper,
                                     Map<ByteArray, List<RegisteredCredential>> credentialMapper) {
        this.credentialMapper = credentialMapper;
        this.handlerMapper = handlerMapper;
    }

    public InMemoryPasskeyRepository() {
        this(new HashMap<>(), new HashMap<>());
    }

    @Override
    public Optional<ByteArray> findUserHandleByEmail(String email) {
        return Optional.ofNullable(handlerMapper.get(email));
    }

    @Override
    public void save(String email, RegisteredCredential credential) {
        ByteArray userId = handlerMapper.computeIfAbsent(email, (k) -> credential.getUserHandle());
        List<RegisteredCredential> registeredCredentials = credentialMapper.computeIfAbsent(userId,
                k -> new ArrayList<>());
        registeredCredentials.add(credential);
    }

    @Override
    public void updateSignatureCount(String email, ByteArray credentialId, long newSignatureCount) {
        ByteArray userId = handlerMapper.get(email);
        if (userId == null) {
            return;
        }

        List<RegisteredCredential> userCredentials = credentialMapper.get(userId);
        if (userCredentials != null) {
            List<RegisteredCredential> updatedCredentials = userCredentials.stream()
                    .map(credential -> updateCredential(credentialId, credential, newSignatureCount))
                    .toList();
            credentialMapper.put(userId, updatedCredentials);
        }
    }

    private RegisteredCredential updateCredential(ByteArray credentialId, RegisteredCredential credential,
                                                  long newSignatureCount) {
        if (credential.getCredentialId().equals(credentialId)) {
            return RegisteredCredential.builder()
                    .credentialId(credential.getCredentialId())
                    .userHandle(credential.getUserHandle())
                    .publicKeyCose(credential.getPublicKeyCose())
                    .signatureCount(newSignatureCount)
                    .build();
        }
        return credential;
    }

    RegisteredCredential findByCredentialIdAndUserHandle(ByteArray credential, ByteArray userHandle) {
        return credentialMapper.get(userHandle).stream()
                .filter(it -> it.getCredentialId().equals(credential))
                .findFirst()
                .get();
    }
}
