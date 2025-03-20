package com.sign.application.repository;

import com.sign.dto.EmailSendResult;

public interface EmailSender {
    EmailSendResult send(String to, String subject, String body);

    boolean ableToSend(String to);
}
