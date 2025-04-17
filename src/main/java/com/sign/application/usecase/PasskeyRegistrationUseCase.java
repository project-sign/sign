package com.sign.application.usecase;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.sign.domain.EmailValidator;
import com.sign.dto.PasskeyRegistrationResult;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasskeyRegistrationUseCase {

    private final PasskeyRepository passkeyRepository;
    private final RelyingParty relyingParty;
    private final HandleGenerator handleGenerator;

    public PasskeyRegistrationResult start(String email) {
        EmailValidator.validateEmailAddress(email);
        ByteArray userHandle = passkeyRepository.findUserHandleByEmail(email)
                .orElseGet(handleGenerator::generateHandle);

        AuthenticatorSelectionCriteria authSelection = AuthenticatorSelectionCriteria.builder()
                .residentKey(ResidentKeyRequirement.REQUIRED)
                .build();

        PublicKeyCredentialCreationOptions options = relyingParty.startRegistration(
                StartRegistrationOptions.builder()
                        .user(UserIdentity.builder()
                                .name(email)
                                .displayName(email)
                                .id(userHandle)
                                .build())
                        .authenticatorSelection(authSelection)
                        .build());
        return PasskeyRegistrationResult.success(options);
    }

    public PasskeyRegistrationResult finish(PublicKeyCredentialCreationOptions options,
                                            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential,
                                            String email) {
        try {
            relyingParty.finishRegistration(FinishRegistrationOptions.builder()
                    .request(options)
                    .response(credential)
                    .build()
            );
            saveCredential(options, credential, email);
        } catch (RegistrationFailedException e) {
            return PasskeyRegistrationResult.failure("패스키 등록에 실패했습니다.");
        }
        return PasskeyRegistrationResult.success();
    }

    private void saveCredential(PublicKeyCredentialCreationOptions options,
                                PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential,
                                String email) {
        RegisteredCredential registeredCredential = RegisteredCredential.builder()
                .credentialId(credential.getId())
                .userHandle(options.getUser().getId())
                .publicKeyCose(getPublicKeyCose(credential))
                .signatureCount(0L)
                .build();
        passkeyRepository.save(email, registeredCredential);
    }

    private ByteArray getPublicKeyCose(
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential) {
        AuthenticatorAttestationResponse response = credential.getResponse();
        return response.getAttestation().getAuthenticatorData().getAttestedCredentialData().get()
                .getCredentialPublicKey();
    }
}
