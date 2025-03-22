package com.sign.infrastructure.repository;

import com.sign.application.repository.CertificationLogger;
import com.sign.dto.EmailCertificationCode;
import com.sign.infrastructure.jpa.CertificationLog;
import com.sign.infrastructure.jpa.repository.CertificationLogJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CertificationLoggerImpl implements CertificationLogger {

    private final CertificationLogJpaRepository jpaRepository;

    @Override
    @Transactional
    public EmailCertificationCode logCertification(EmailCertificationCode emailCertificationCode) {
        jpaRepository.save(
                CertificationLog.builder()
                        .email(emailCertificationCode.email())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        return emailCertificationCode;
    }

    @Override
    @Transactional
    public Optional<LocalDateTime> lastCreatedAtFor(String email) {
        return jpaRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .map(CertificationLog::getCreatedAt);

    }
}
