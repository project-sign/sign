package com.sign.infrastructure.jpa.repository;


import com.sign.infrastructure.jpa.PasskeyEntity;
import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasskeyJpaRepository extends JpaRepository<PasskeyEntity, Long> {

    Optional<PasskeyEntity> findByEmail(String email);

    Optional<PasskeyEntity> findByUserHandle(ByteArray userHandle);
}
