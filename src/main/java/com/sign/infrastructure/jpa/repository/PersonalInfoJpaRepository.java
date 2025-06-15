package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.PersonalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalInfoJpaRepository extends JpaRepository<PersonalInfoEntity, Integer> {
}
