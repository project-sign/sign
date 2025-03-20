package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailSender;
import com.sign.dto.EmailCertificationSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EmailSenderImpl implements EmailSender {
    @Override
    public EmailCertificationSendResult send(String to, String subject, String body) {
        log.info("send email to {}", to);
        return new EmailCertificationSendResult(to, 1, 1);
    }
}
