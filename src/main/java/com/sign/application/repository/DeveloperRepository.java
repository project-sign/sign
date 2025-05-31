package com.sign.application.repository;

import com.sign.domain.Developer;

import java.util.Optional;

public interface DeveloperRepository {

    Optional<Developer> saveDeveloper(Developer developer);

    Optional<Developer> findDeveloperByEmail(String email);
}
