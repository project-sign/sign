package com.sign.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.sign.dto.PasskeyAssertionResult;
import com.sign.dto.PasskeyRegistrationResult;
import com.sign.infrastructure.repository.HandleGeneratorImpl;
import com.sign.infrastructure.repository.InMemoryPasskeyRepository;
import com.sign.infrastructure.yubico.repository.InMemoryCredentialRepository;
import com.sign.support.fixture.RelyingPartyFixture;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import de.adesso.softauthn.Authenticators;
import de.adesso.softauthn.CredentialsContainer;
import de.adesso.softauthn.Origin;
import de.adesso.softauthn.authenticator.WebAuthnAuthenticator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PasskeyAssertionUseCaseTest {

    private final Map<String, ByteArray> handlerMapper = new HashMap<>();
    private final Map<ByteArray, List<RegisteredCredential>> credentialMapper = new HashMap<>();
    private final PasskeyRepository passkeyRepository = new InMemoryPasskeyRepository(handlerMapper, credentialMapper);
    private final CredentialRepository credentialRepository = new InMemoryCredentialRepository(handlerMapper,
            credentialMapper);
    private final RelyingParty relyingParty = RelyingPartyFixture.create(credentialRepository);
    private final PasskeyAssertionUseCase passkeyAssertionUseCase = new PasskeyAssertionUseCase(relyingParty,
            passkeyRepository);

    private final String email = "passkey@sign.co.kr";
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
        PasskeyRegistrationResult result = passkeyRegistrationUseCase.start(email);
        PublicKeyCredential<AuthenticatorAttestationResponse,
                ClientRegistrationExtensionOutputs> credential = container.create(result.getOptions());
        passkeyRegistrationUseCase.finishInternal(result.getOptions(), credential, email);
    }

    @Test
    @DisplayName("등록된 패스키로 인증에 성공한다.")
    void test1() {
        AssertionRequest request = passkeyAssertionUseCase.start();
        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> response = container.get(
                request.getPublicKeyCredentialRequestOptions());

        PasskeyAssertionResult result = passkeyAssertionUseCase.finish(request, response);
        boolean actual = result.getStatus().isSuccess();

        assertThat(actual).isTrue();
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
        @DisplayName("credential과 다른 challenge로 로그인 할 경우 실패한다.")
        void test1() {
            PasskeyAssertionResult result = passkeyAssertionUseCase.finish(request, otherResponse);
            boolean actual = result.getStatus().isSuccess();
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("challenge와 다른 credential로 로그인 할 경우 실패한다.")
        void test2() {
            PasskeyAssertionResult result = passkeyAssertionUseCase.finish(otherRequest, response);
            boolean actual = result.getStatus().isSuccess();
            assertThat(actual).isFalse();
        }
    }
}
