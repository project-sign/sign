package com.sign.application.repository;

import com.sign.dto.EmailCertificationCode;
import java.util.Optional;

public interface EmailCertificationRepository {
    EmailCertificationCode save(EmailCertificationCode emailCertificationCode);

    Optional<EmailCertificationCode> findByEmail(String emailAddress);
    
    void deleteByEmail(String emailAddress);
}
