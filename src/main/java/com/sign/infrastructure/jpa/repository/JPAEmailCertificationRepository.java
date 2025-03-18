package com.sign.infrastructure.jpa.repository;

import com.sign.infrastructure.jpa.entity.EmailCertification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAEmailCertificationRepository extends JpaRepository<EmailCertification, Integer> {

    EmailCertification findByEmailAddress(String emailAddress);
}
