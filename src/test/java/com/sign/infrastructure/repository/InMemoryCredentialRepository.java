package com.sign.infrastructure.repository;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryCredentialRepository implements CredentialRepository {

    private final Map<String, ByteArray> handlerMapper;
    private final Map<ByteArray, List<RegisteredCredential>> credentialMapper;

    public InMemoryCredentialRepository(Map<String, ByteArray> handlerMapper,
                                        Map<ByteArray, List<RegisteredCredential>> credentialMapper) {
        this.credentialMapper = credentialMapper;
        this.handlerMapper = handlerMapper;
    }

    public InMemoryCredentialRepository() {
        this(new HashMap<>(), new HashMap<>());
    }

    public void save(String email, RegisteredCredential credential) {
        ByteArray userId = handlerMapper.computeIfAbsent(email, (k) -> credential.getUserHandle());
        List<RegisteredCredential> registeredCredentials = credentialMapper.computeIfAbsent(userId,
                k -> new ArrayList<>());
        registeredCredentials.add(credential);
    }

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        ByteArray userId = handlerMapper.get(username);
        if (userId == null) {
            return Collections.emptySet();
        }
        return credentialMapper.getOrDefault(userId, Collections.emptyList()).stream()
                .map(registeredCredential ->
                        PublicKeyCredentialDescriptor.builder()
                                .id(registeredCredential.getCredentialId())
                                .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.ofNullable(handlerMapper.get(username));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return handlerMapper.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userHandle))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return credentialMapper.values().stream()
                .flatMap(Collection::stream)
                .filter(cred -> cred.getCredentialId().equals(credentialId))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return credentialMapper.getOrDefault(userHandle, Collections.emptyList()).stream()
                .filter(cred -> cred.getCredentialId().equals(credentialId))
                .findFirst();
    }
}
