package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailSender;
import com.sign.dto.EmailSendResult;
import org.springframework.stereotype.Repository;

@Repository
public class EmailSenderImpl implements EmailSender {
    @Override
    public EmailSendResult send(String to, String subject, String body) {
        return EmailSendResult.success("", to, subject);
    }
}
