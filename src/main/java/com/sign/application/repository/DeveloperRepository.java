package com.sign.application.repository;

import com.sign.domain.Developer;
import java.time.LocalDateTime;
import java.util.Optional;

public interface DeveloperRepository {

    Optional<Developer> findByEmail(String email);

    void save(String email, LocalDateTime agreedAt);
}
