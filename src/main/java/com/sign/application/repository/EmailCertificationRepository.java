package com.sign.application.repository;

import com.sign.dto.EmailCertificationCode;

public interface EmailCertificationRepository {
    EmailCertificationCode save(EmailCertificationCode emailCertificationCode);
}
