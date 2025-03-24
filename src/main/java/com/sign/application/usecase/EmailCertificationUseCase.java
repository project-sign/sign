package com.sign.application.usecase;

import com.sign.application.repository.EmailCertificationLogger;
import com.sign.application.repository.EmailCertificationRepository;
import com.sign.application.repository.EmailSender;
import com.sign.application.repository.RandomCodeGenerator;
import com.sign.application.usecase.config.EmailCertificationProperties;
import com.sign.dto.EmailCertificationCode;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailSendResult;
import com.sign.dto.EmailValidationRequest;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailCertificationUseCase {

    private final EmailCertificationRepository emailCertificationRepository;
    private final EmailSender emailSender;
    private final RandomCodeGenerator codeGenerator;
    private final EmailCertificationLogger emailCertificationLogger;

    private final EmailCertificationProperties emailCertificationProperties;

    private final Clock clock;

    public EmailSendResult sendCertification(EmailCertificationRequest param) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime certificationLastCreatedAt = emailCertificationLogger.lastCreatedAtFor(param.email())
                .orElse(now.minusSeconds(emailCertificationProperties.reSendTimeAsSeconds())); // 만약 로그가 없다면 메일을 보내야 한다.
        Duration between = Duration.between(certificationLastCreatedAt, now);
        if (between.getSeconds() < emailCertificationProperties.reSendTimeAsSeconds()) {
            return EmailSendResult.failure(emailCertificationProperties.mailHost(), param.email(), "Sign 인증 번호",
                    "아직 인증 메일을 보낼 수 없습니다.");
        }

        String code = codeGenerator.generate(6);

        EmailCertificationCode certificationCode = emailCertificationRepository.save(
                new EmailCertificationCode(param.email(), code,
                        now.plusSeconds(emailCertificationProperties.expiredTimeAsSeconds()))
        );
        emailCertificationLogger.logCertification(certificationCode);

        return emailSender.send(emailCertificationProperties.mailHost(), param.email(), "Sign 인증 번호", code);
    }

    public boolean validateCertification(EmailValidationRequest param) {
        LocalDateTime now = LocalDateTime.now(clock);
        return emailCertificationRepository.findByEmail(param.email())
                .filter(it -> it.certificationCode().equals(param.code()))
                .filter(it -> !now.isAfter(it.expiredAt()))
                .isPresent();
    }
}
