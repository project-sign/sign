package com.sign.application.usecase;

import com.sign.dto.DeveloperLoginRequest;
import com.sign.dto.DeveloperLoginResult;
import com.sign.dto.PasskeyAssertionResult;
import com.sign.dto.Status;
import com.sign.infrastructure.repository.DeveloperRepositoryImpl;
import com.yubico.webauthn.AssertionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeveloperLoginUseCase {

    private static final String FAIL_REASON = "아이디 존재하지 않거나, 인증에 실패했습니다.";

    private final DeveloperRepositoryImpl repository;
    private final PasskeyAssertionUseCase passkeyAssertionUseCase;

    public DeveloperLoginResult login(AssertionRequest options, DeveloperLoginRequest request) {
        PasskeyAssertionResult assertionResult = passkeyAssertionUseCase.finish(options, request.credential());
        if (assertionResult.getStatus() == Status.SUCCESS) {
            return findDeveloperByEmail(request);
        }
        return DeveloperLoginResult.failure(FAIL_REASON);
    }

    private DeveloperLoginResult findDeveloperByEmail(DeveloperLoginRequest request) {
        return repository.findByEmail(request.email())
                .map((it) -> DeveloperLoginResult.success(it.getEmail()))
                .orElseGet(() -> DeveloperLoginResult.failure(FAIL_REASON));
    }
}
