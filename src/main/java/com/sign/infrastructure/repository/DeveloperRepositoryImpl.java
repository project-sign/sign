package com.sign.infrastructure.repository;

import com.sign.application.repository.DeveloperRepository;
import com.sign.domain.Developer;
import com.sign.infrastructure.jpa.DeveloperEntity;
import com.sign.infrastructure.jpa.repository.DeveloperJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeveloperRepositoryImpl implements DeveloperRepository {

    private final DeveloperJpaRepository developerJpaRepository;

    @Override
    public Optional<Developer> saveDeveloper(Developer developer) {
        DeveloperEntity developerEntity = DeveloperEntity.builder()
                .email(developer.email())
                .build();
        developerJpaRepository.save(developerEntity);
        return Optional.of(developer);
    }

    @Override
    public Optional<Developer> findDeveloperByEmail(String email) {
        return developerJpaRepository.findByEmail(email).map(it -> new Developer(it.getEmail()));
    }
}
