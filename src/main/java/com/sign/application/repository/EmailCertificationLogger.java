package com.sign.application.repository;

import com.sign.dto.EmailCertificationCode;
import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailCertificationLogger {

    EmailCertificationCode logCertification(EmailCertificationCode emailCertificationCode);

    Optional<LocalDateTime> lastCreatedAtFor(String email);
}
