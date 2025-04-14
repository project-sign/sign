package com.sign.controller;

import com.sign.application.usecase.EmailCertificationUseCase;
import com.sign.controller.argumentresolver.ResponseProtector;
import com.sign.dto.APIResponse;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailSendResult;
import com.sign.dto.EmailValidationRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/email/certification")
public class EmailCertificationController {

    private final EmailCertificationUseCase emailCertificationUseCase;
    private final ResponseProtector responseProtector;
    private final CookieManager cookieManager;

    @PostMapping
    public APIResponse<EmailSendResult> sendCertification(@RequestBody EmailCertificationRequest param) {
        EmailSendResult emailSendResult = emailCertificationUseCase.sendCertification(param);
        return new APIResponse<>(
                "인증 메일 전송 결과",
                emailSendResult
        );
    }

    @PostMapping("/validation")
    public APIResponse<Boolean> validateCertification(@RequestBody EmailValidationRequest param,
                                                      HttpServletResponse response) {
        boolean isSuccess = emailCertificationUseCase.validateCertification(param);
        if (isSuccess) {
            String encrypted = responseProtector.encrypt(param.email());
            ResponseCookie emailToken = cookieManager.provide("email_token", encrypted);
            response.setHeader(HttpHeaders.SET_COOKIE, emailToken.toString());
        }
        return new APIResponse<>(
                "이메일 인증 결과",
                isSuccess
        );
    }
}
