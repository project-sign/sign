package com.sign.infrastructure.repository;

import com.sign.application.repository.PersonalInfoAgreeRepository;
import com.sign.infrastructure.jpa.PersonalInfoEntity;
import com.sign.infrastructure.jpa.repository.PersonalInfoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PersonalInfoAgreeRepositoryImpl implements PersonalInfoAgreeRepository {

    private final PersonalInfoJpaRepository jpaRepository;

    @Override
    public void save(String email, boolean agree) {
        jpaRepository.save(PersonalInfoEntity.builder().email(email).agree(agree).build());
    }
}
