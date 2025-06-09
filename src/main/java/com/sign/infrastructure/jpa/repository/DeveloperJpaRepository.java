package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.DeveloperEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperJpaRepository extends JpaRepository<DeveloperEntity, Long> {

    Optional<DeveloperEntity> findByEmail(String email);
}
