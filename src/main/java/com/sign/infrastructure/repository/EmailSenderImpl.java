package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailSender;
import com.sign.dto.EmailSendResult;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public EmailSendResult send(String from, String to, String subject, String body) {
        try {
            sendMailWithJavaMailSender(to, subject, body);
        } catch (MessagingException e) {
            log.info(e.toString());
            return EmailSendResult.failure(from, to, subject, e.getMessage());
        }
        return EmailSendResult.success(from, to, subject);
    }

    private void sendMailWithJavaMailSender(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        javaMailSender.send(message);
    }
}
