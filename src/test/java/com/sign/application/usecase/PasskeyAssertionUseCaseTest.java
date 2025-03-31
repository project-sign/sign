package com.sign.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.sign.dto.PasskeyAssertionResult;
import com.sign.infrastructure.repository.HandleGeneratorImpl;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import de.adesso.softauthn.Authenticators;
import de.adesso.softauthn.CredentialsContainer;
import de.adesso.softauthn.Origin;
import de.adesso.softauthn.authenticator.WebAuthnAuthenticator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PasskeyAssertionUseCaseTest {

    private final String email = "passkey@sign.co.kr";
    private final PasskeyRepository passkeyRepository = new InMemoryPasskeyRepository();
    private final RelyingParty relyingParty = RelyingPartyFixture.create(passkeyRepository);
    private final PasskeyAssertionUseCase passkeyAssertionUseCase = new PasskeyAssertionUseCase(relyingParty,
            passkeyRepository);
    private final WebAuthnAuthenticator authenticator = Authenticators.yubikey5Nfc().build();
    private final PasskeyRegistrationUseCase passkeyRegistrationUseCase;
    private final HandleGenerator handleGenerator = new HandleGeneratorImpl(32);
    private final Origin origin = new Origin("https", "sign.co.kr", -1, null);
    private final CredentialsContainer container = new CredentialsContainer(origin, List.of(authenticator));

    PasskeyAssertionUseCaseTest() {
        passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(passkeyRepository, relyingParty, handleGenerator);
    }

    @BeforeEach
    void setUp() {
        PublicKeyCredentialCreationOptions options = passkeyRegistrationUseCase.start(email);
        PublicKeyCredential<AuthenticatorAttestationResponse,
                ClientRegistrationExtensionOutputs> credential = container.create(options);
        passkeyRegistrationUseCase.finish(options, credential, email);
    }

    @Test
    @DisplayName("등록된 패스키로 인증에 성공한다.")
    void test1() {
        AssertionRequest request = passkeyAssertionUseCase.start();
        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> response = container.get(
                request.getPublicKeyCredentialRequestOptions());

        PasskeyAssertionResult finish = passkeyAssertionUseCase.finish(request, response);
        String actual = finish.getEmail();

        assertThat(actual).isEqualTo(email);
    }


    @Nested
    @DisplayName("등록과 정보와 일치하지 않는 정보가 주어질 때")
    class WhenDifferentChallenge {
        private AssertionRequest request;
        private AssertionRequest otherRequest;
        private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> response;
        private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> otherResponse;

        @BeforeEach
        void setUp() {
            request = passkeyAssertionUseCase.start();
            otherRequest = passkeyAssertionUseCase.start();
            response = container.get(request.getPublicKeyCredentialRequestOptions());
            otherResponse = container.get(otherRequest.getPublicKeyCredentialRequestOptions());
        }

        @Test
        @DisplayName("credential과 다른 challenge로 로그인 할 경우 예외가 발생한다.")
        void test1() {
            assertThatThrownBy(() -> passkeyAssertionUseCase.finish(request, otherResponse));
        }

        @Test
        @DisplayName("challenge와 다른 credential로 로그인 할 경우 예외가 발생한다.")
        void test2() {
            assertThatThrownBy(() -> passkeyAssertionUseCase.finish(otherRequest, response));
        }
    }
}
