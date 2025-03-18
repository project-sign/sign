package com.sign.application.usecase;

import com.sign.application.repository.EmailCertificationRepository;
import com.sign.application.repository.EmailSender;
import com.sign.application.repository.RandomCodeGenerator;
import com.sign.domain.EmailValidator;
import com.sign.dto.EmailCertificationCode;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailSendResult;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCertificationUseCase {

    private final EmailCertificationRepository emailCertificationRepository;
    private final EmailSender emailSender;
    private final RandomCodeGenerator codeGenerator;

    public void sendCertification(EmailCertificationRequest param) {
        EmailValidator.validateEmailAddress(param.email());

        Random random = new Random();
        String code = codeGenerator.generate(6);
        EmailCertificationCode saved = emailCertificationRepository.save(
                new EmailCertificationCode(param.email(), code));

        // TODO 이메일 양식 설정 필요
        EmailSendResult emailSendResult = emailSender.send(saved.email(), "인증 코드 발송", saved.certificationCode());
        log.info(emailSendResult.toString());
    }
}
