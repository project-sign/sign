package com.sign.application.usecase;

import com.sign.application.repository.CertificationLogger;
import com.sign.application.repository.EmailCertificationRepository;
import com.sign.application.repository.EmailSender;
import com.sign.application.repository.RandomCodeGenerator;
import com.sign.dto.EmailCertificationCode;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailSendResult;
import com.sign.dto.EmailValidationRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EmailCertificationUseCase {

    private final EmailCertificationRepository emailCertificationRepository;
    private final EmailSender emailSender;
    private final RandomCodeGenerator codeGenerator;
    private final CertificationLogger certificationLogger;

    /*
    TODO 이메일 전송 제한 구현, 응답에 이메일 재전송 요청 가능 시간, 만료 시간 적기
    이메일 타임아웃 5분
    이메일 재시도 횟수 - 1분에 1개
    인증 코드 형태 - 숫자 6자리
     */
    public EmailSendResult sendCertification(EmailCertificationRequest param) {
        // TODO 이메일 전송이 가능하지 않으면 실패 응답 + 언제부터 보낼 수 있는지 응답
        // TODO 이메일 전송이 가능하면 6자리 인증 코드 + 만료 시간 저장 후 이메일 전송
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime certificationLastCreatedAt = certificationLogger.lastCreatedAtFor(param.email());
        Duration between = Duration.between(certificationLastCreatedAt, now);
        if (between.getSeconds() < 60) {
            return EmailSendResult.failure("test@sign.co.kr", param.email(), "Sign 인증 번호", "아직 인증 메일을 보낼 수 없습니다.");
        }
        String code = codeGenerator.generate(6);
        emailCertificationRepository.save(new EmailCertificationCode(param.email(), code, now.plusMinutes(5)));
        return emailSender.send(param.email(), "Sign 인증 번호", code);
    }

    public boolean validateCertification(EmailValidationRequest param) {
        return emailCertificationRepository.findByEmail(param.email())
                .certificationCode()
                .equals(param.code());
    }
}
