package com.sign.application.repository;

public interface EmailSender {
    void send(String from, String to, String subject, String body);
}
