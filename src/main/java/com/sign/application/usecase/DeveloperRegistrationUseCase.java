package com.sign.application.usecase;

import com.sign.dto.DeveloperRegistrationRequest;
import com.sign.dto.DeveloperRegistrationResult;
import com.sign.dto.PasskeyAssertionResult;
import com.sign.dto.Status;
import com.sign.infrastructure.repository.DeveloperRepositoryImpl;
import com.yubico.webauthn.AssertionRequest;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeveloperRegistrationUseCase {

    private final DeveloperRepositoryImpl repository;
    private final PasskeyAssertionUseCase passkeyAssertionUseCase;
    private final Clock clock;

    public DeveloperRegistrationResult registerDeveloper(AssertionRequest options,
                                                         DeveloperRegistrationRequest request) {
        PasskeyAssertionResult assertionResult = passkeyAssertionUseCase.finish(options, request.credential());
        if (assertionResult.getStatus() == Status.SUCCESS) {
            return register(request);
        }
        return DeveloperRegistrationResult.failure(assertionResult.getFailReason());
    }

    private DeveloperRegistrationResult register(DeveloperRegistrationRequest request) {
        if (request.agree()) {
            repository.save(request.email(), LocalDateTime.now(clock));
            return DeveloperRegistrationResult.success();
        }
        return DeveloperRegistrationResult.failure("회원 가입에 동의하지 않았습니다.");
    }
}
