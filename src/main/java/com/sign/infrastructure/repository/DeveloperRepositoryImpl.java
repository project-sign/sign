package com.sign.infrastructure.repository;

import com.sign.application.repository.DeveloperRepository;
import com.sign.domain.Developer;
import com.sign.infrastructure.jpa.repository.DeveloperJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeveloperRepositoryImpl implements DeveloperRepository {

    private final DeveloperJpaRepository jpaRepository;

    @Override
    public Optional<Developer> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map((it) -> new Developer(it.getEmail()));
    }
}
