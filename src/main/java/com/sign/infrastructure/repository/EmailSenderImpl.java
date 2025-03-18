package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailSender;
import com.sign.dto.EmailSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EmailSenderImpl implements EmailSender {
    @Override
    public EmailSendResult send(String to, String subject, String body) {
        log.info("send email to {}", to);
        return new EmailSendResult();
    }
}
