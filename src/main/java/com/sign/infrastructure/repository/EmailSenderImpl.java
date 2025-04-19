package com.sign.infrastructure.repository;

import com.sign.application.repository.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender javaMailSender;

    private final Executor executor;

    // Qualifier 를 사용하기 위해서는 롬복을 이용할 수 없습니다.
    public EmailSenderImpl(JavaMailSender javaMailSender, @Qualifier("emailTaskExecutor") Executor executor) {
        this.javaMailSender = javaMailSender;
        this.executor = executor;
    }

    @Override
    public void send(String from, String to, String subject, String body) {
        executor.execute(() -> sendMailWithJavaMailSender(from, to, subject, body));
    }

    private void sendMailWithJavaMailSender(String from, String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.info(e.toString());
        }
    }
}
