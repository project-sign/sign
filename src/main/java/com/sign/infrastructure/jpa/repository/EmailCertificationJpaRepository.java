package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.EmailCertificationCodeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailCertificationJpaRepository extends JpaRepository<EmailCertificationCodeEntity, Long> {

    Optional<EmailCertificationCodeEntity> findByEmail(String email);
}
