package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailCertificationTryLogger;
import com.sign.infrastructure.jpa.CertificationTryLogEntity;
import com.sign.infrastructure.jpa.repository.CertificationTryLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EmailCertificationTryLoggerImpl implements EmailCertificationTryLogger {

    private final CertificationTryLogRepository certificationTryLogRepository;

    @Override
    public void saveTryCount(String email, int tryCount) {
        Long targetId = certificationTryLogRepository.findByEmail(email)
                .map(CertificationTryLogEntity::getId).orElse(null);
        certificationTryLogRepository.save(
                CertificationTryLogEntity.builder()
                        .id(targetId)
                        .email(email)
                        .tryCount(tryCount)
                        .build()
        );
    }

    @Override
    public int findCertificationTryCount(String email) {
        return certificationTryLogRepository.findByEmail(email)
                .map(CertificationTryLogEntity::getTryCount)
                .orElse(0);
    }
}
