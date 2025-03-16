package com.sign.application.usecase;

import com.sign.application.repository.EmailCertificationRepository;
import com.sign.application.repository.EmailSender;
import com.sign.application.repository.RandomCodeGenerator;
import com.sign.dto.EmailCertificationCode;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailSendResult;
import java.util.Random;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EmailCertificationUseCase {

    private EmailCertificationRepository emailCertificationRepository;
    private EmailSender emailSender;
    private RandomCodeGenerator codeGenerator;

    private static final String EMAIL_REGEX = "^(?!\\.)[a-zA-Z0-9._%+-]+(?<!\\.)@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public void sendCertification(EmailCertificationRequest param) {
        validateEmailAddress(param);
        Random random = new Random();
        String code = codeGenerator.generate(6);
        EmailCertificationCode saved = emailCertificationRepository.save(
                new EmailCertificationCode(param.email(), code));
        // TODO 이메일 양식 설정 필요
        EmailSendResult emailSendResult = emailSender.send(saved.email(), "인증 코드 발송", saved.certificationCode());
        log.info(emailSendResult.toString());
    }

    private void validateEmailAddress(EmailCertificationRequest param) {
        String email = param.email();
        if (email != null && EMAIL_PATTERN.matcher(email).matches()) {
            return;
        }
        throw new IllegalArgumentException("잘못된 이메일 주소입니다.");
    }
}
