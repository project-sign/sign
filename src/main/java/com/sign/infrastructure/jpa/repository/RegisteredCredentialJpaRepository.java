package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.PasskeyEntity;
import com.sign.infrastructure.jpa.RegisteredCredentialEntity;
import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredCredentialJpaRepository extends JpaRepository<RegisteredCredentialEntity, Long> {

    Optional<RegisteredCredentialEntity> findByCredentialId(ByteArray credentialId);

    Optional<RegisteredCredentialEntity> findByCredentialIdAndUserHandle(ByteArray credentialId, ByteArray userHandle);

    Set<RegisteredCredentialEntity> findByPasskey(PasskeyEntity passkey);
}
