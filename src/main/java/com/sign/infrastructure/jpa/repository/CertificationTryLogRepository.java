package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.CertificationTryLogEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationTryLogRepository extends JpaRepository<CertificationTryLogEntity, Long> {
    Optional<CertificationTryLogEntity> findByEmail(String email);
}
