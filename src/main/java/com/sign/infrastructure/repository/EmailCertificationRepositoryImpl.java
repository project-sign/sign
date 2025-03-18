package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailCertificationRepository;
import com.sign.dto.EmailCertificationCode;
import com.sign.infrastructure.jpa.entity.EmailCertification;
import com.sign.infrastructure.jpa.repository.JPAEmailCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class EmailCertificationRepositoryImpl implements EmailCertificationRepository {

    private final JPAEmailCertificationRepository jpaEmailCertificationRepository;

    @Override
    @Transactional
    public EmailCertificationCode save(EmailCertificationCode emailCertificationCode) {
        EmailCertification emailCertification = jpaEmailCertificationRepository.save(
                new EmailCertification(emailCertificationCode.email(), emailCertificationCode.certificationCode())
        );

        return new EmailCertificationCode(
                emailCertificationCode.email(), emailCertificationCode.certificationCode()
        );
    }

    @Override
    public EmailCertificationCode findByEmail(String emailAddress) {
        EmailCertification jpaEntity = jpaEmailCertificationRepository.findByEmailAddress(emailAddress);

        return new EmailCertificationCode(
                jpaEntity.getEmailAddress(), jpaEntity.getCertificationCode()
        );
    }
}
