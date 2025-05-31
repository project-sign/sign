package com.sign.application.service;

import com.sign.application.usecase.DeveloperUseCase;
import com.sign.application.usecase.PasskeyRegistrationUseCase;
import com.sign.dto.PasskeyRegistrationResult;
import com.yubico.webauthn.data.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeveloperService {
    private final DeveloperUseCase developerUseCase;
    private final PasskeyRegistrationUseCase passkeyRegistrationUseCase;

    public PasskeyRegistrationResult start(String email) {
        return passkeyRegistrationUseCase.start(email);
    }

    public PasskeyRegistrationResult finish(PublicKeyCredentialCreationOptions options,
                                            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential,
                                            String email) {

        PasskeyRegistrationResult result = passkeyRegistrationUseCase.finish(options, credential, email);
        if (result.isSuccess()) {
            developerUseCase.saveDeveloper(email);
        }
        return result;
    }
}
