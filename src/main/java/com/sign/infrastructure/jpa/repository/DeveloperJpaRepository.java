package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.DeveloperEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeveloperJpaRepository extends JpaRepository<DeveloperEntity, Long> {

    Optional<DeveloperEntity> findByEmail(String email);
}
