package com.sign.application.usecase;

import com.sign.application.repository.EmailCertificationLogger;
import com.sign.application.repository.EmailCertificationRepository;
import com.sign.application.repository.EmailCertificationTryLogger;
import com.sign.application.repository.EmailSender;
import com.sign.application.repository.RandomCodeGenerator;
import com.sign.application.usecase.config.EmailCertificationProperties;
import com.sign.domain.EmailValidator;
import com.sign.dto.EmailCertificationCode;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailSendResult;
import com.sign.dto.EmailValidationRequest;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
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
    private final EmailCertificationTryLogger emailCertificationTryLogger;
    private final EmailCertificationProperties emailCertificationProperties;

    private final Clock clock;

    public EmailSendResult sendCertification(EmailCertificationRequest param) {
        EmailValidator.validateEmailAddress(param.email());

        LocalDateTime now = LocalDateTime.now(clock);

        String subject = "Sign 인증 번호";

        if (checkEmailReSendTime(param, now)) {
            return EmailSendResult.failure(
                    emailCertificationProperties.mailHost(),
                    param.email(), subject,
                    "아직 인증 메일을 보낼 수 없습니다."
            );
        }

        String code = codeGenerator.generate(6);

        emailCertificationRepository.deleteByEmail(param.email());
        EmailCertificationCode certificationCode = emailCertificationRepository.save(
                new EmailCertificationCode(param.email(), code,
                        now.plusSeconds(emailCertificationProperties.expiredTimeAsSeconds()))
        );
        emailCertificationLogger.logCertification(certificationCode);

        emailSender.send(emailCertificationProperties.mailHost(), param.email(), subject, code);

        return EmailSendResult.success(emailCertificationProperties.mailHost(), param.email(), subject);
    }

    private boolean checkEmailReSendTime(EmailCertificationRequest param, LocalDateTime now) {
        LocalDateTime certificationLastCreatedAt = emailCertificationLogger.lastCreatedAtFor(param.email())
                .orElse(now.minusSeconds(emailCertificationProperties.reSendTimeAsSeconds())); // 만약 로그가 없다면 메일을 보내야 한다.
        Duration between = Duration.between(certificationLastCreatedAt, now);
        return between.getSeconds() < emailCertificationProperties.reSendTimeAsSeconds();
    }

    public boolean validateCertification(EmailValidationRequest param) {
        int certificationTryCount = emailCertificationTryLogger.findCertificationTryCount(param.email());
        if (certificationTryCount > emailCertificationProperties.maxCertificationCount()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now(clock);
        Optional<EmailCertificationCode> emailCertificationCode = emailCertificationRepository.findByEmail(
                param.email());
        boolean isValid = emailCertificationCode
                .filter(it -> it.certificationCode().equals(param.code()))
                .filter(it -> !now.isAfter(it.expiredAt()))
                .isPresent();
        if (isValid) {
            emailCertificationTryLogger.saveTryCount(param.email(), 0);
            return true;
        }
        emailCertificationTryLogger.saveTryCount(param.email(), certificationTryCount + 1);
        return false;
    }
}
