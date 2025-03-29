package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.CertificationLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationLogJpaRepository extends JpaRepository<CertificationLog, Long> {

    Optional<CertificationLog> findTopByEmailOrderByCreatedAtDesc(String email);
}
