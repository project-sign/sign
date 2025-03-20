package com.sign.controller;

import com.sign.application.usecase.EmailCertificationUseCase;
import com.sign.dto.APIResponse;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailCertificationSendResult;
import com.sign.dto.EmailValidationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailCertificationController {
    private final EmailCertificationUseCase emailCertificationUseCase;

    @PostMapping("/certification")
    public ResponseEntity<APIResponse<EmailCertificationSendResult>> sendCertification(
            @RequestBody EmailCertificationRequest param) {
        EmailCertificationSendResult emailCertificationSendResult = emailCertificationUseCase.sendCertification(param);
        return ResponseEntity.ok(new APIResponse<>("%s로 인증 코드를 전송했습니다.".formatted(param.email()),
                emailCertificationSendResult));
    }

    @PostMapping("/validation")
    public ResponseEntity<APIResponse<Boolean>> validateCertification(@RequestBody EmailValidationRequest param) {
        return ResponseEntity.ok(
                new APIResponse<>("검증 결과가 나왔습니다.", emailCertificationUseCase.validateCertification(param))
        );
    }
}
