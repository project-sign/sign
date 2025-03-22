package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailCertificationRepository;
import com.sign.dto.EmailCertificationCode;
import com.sign.infrastructure.jpa.EmailCertificationCodeEntity;
import com.sign.infrastructure.jpa.repository.EmailCertificationJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class EmailCertificationRepositoryImpl implements EmailCertificationRepository {

    private final EmailCertificationJpaRepository jpaRepository;

    @Override
    @Transactional
    public EmailCertificationCode save(EmailCertificationCode emailCertificationCode) {
        EmailCertificationCodeEntity saved = jpaRepository.save(
                EmailCertificationCodeEntity.builder()
                        .email(emailCertificationCode.email())
                        .certificationCode(emailCertificationCode.certificationCode())
                        .expiredAt(emailCertificationCode.expiredAt())
                        .build()
        );
        return new EmailCertificationCode(saved.getEmail(), saved.getCertificationCode(), saved.getExpiredAt());
    }

    @Override
    @Transactional
    public Optional<EmailCertificationCode> findByEmail(String emailAddress) {
        Optional<EmailCertificationCodeEntity> entity = jpaRepository.findByEmail(emailAddress);
        LocalDateTime now = LocalDateTime.now();
        return entity.stream().filter(it -> !it.getExpiredAt().isAfter(now))
                .map(it -> new EmailCertificationCode(it.getEmail(), it.getCertificationCode(), it.getExpiredAt()))
                .findFirst();
    }
}
