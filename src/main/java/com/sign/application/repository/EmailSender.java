package com.sign.application.repository;

import com.sign.dto.EmailCertificationSendResult;

public interface EmailSender {
    EmailCertificationSendResult send(String to, String subject, String body);
}
