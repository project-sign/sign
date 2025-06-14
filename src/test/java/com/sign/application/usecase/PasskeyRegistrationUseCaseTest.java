package com.sign.application.usecase;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.sign.dto.PasskeyRegistrationRequest;
import com.sign.dto.PasskeyRegistrationResult;
import com.sign.infrastructure.repository.HandleGeneratorImpl;
import com.sign.infrastructure.repository.InMemoryPasskeyRepository;
import com.sign.infrastructure.yubico.repository.InMemoryCredentialRepository;
import com.sign.support.fixture.RelyingPartyFixture;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.*;
import de.adesso.softauthn.Authenticators;
import de.adesso.softauthn.CredentialsContainer;
import de.adesso.softauthn.Origin;
import de.adesso.softauthn.authenticator.WebAuthnAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasskeyRegistrationUseCaseTest {

    private final String email = "passkey@sign.co.kr";
    private final PasskeyRepository passkeyRepository = Mockito.mock(PasskeyRepository.class);
    private final CredentialRepository credentialRepository = Mockito.mock(CredentialRepository.class);

    @Nested
    @DisplayName("Registration Value 테스트")
    class Test1 {
        private final RelyingParty relyingParty = RelyingPartyFixture.create(credentialRepository);
        private final HandleGenerator handleGenerator = new HandleGeneratorImpl(32);
        private final PasskeyRegistrationUseCase passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(
                passkeyRepository,
                relyingParty,
                handleGenerator
        );

        @Test
        @DisplayName("같은 이메일을 전달하면 서로 다른 challenge값이 생성된다.")
        void test1() {
            PasskeyRegistrationResult result1 = passkeyRegistrationUseCase.start(email);
            PasskeyRegistrationResult result2 = passkeyRegistrationUseCase.start(email);

            ByteArray challenge1 = result1.getOptions().getChallenge();
            ByteArray challenge2 = result2.getOptions().getChallenge();

            assertThat(challenge1.getBase64Url()).isNotEqualTo(challenge2.getBase64Url());
        }
    }

    @Nested
    @DisplayName("Registration Start 테스트")
    class WhenRegistrationStart {
        private final RelyingParty relyingParty = Mockito.mock(RelyingParty.class);
        private final HandleGenerator handleGenerator = Mockito.mock(HandleGenerator.class);

        private final PasskeyRegistrationUseCase passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(
                passkeyRepository,
                relyingParty,
                handleGenerator
        );

        @Test
        @DisplayName("이메일 값이 존재하지 않으면 예외가 발생한다.")
        void test1() {
            assertThatThrownBy(() -> passkeyRegistrationUseCase.start(null));
        }
    }

    @Nested
    @DisplayName("Registration Finish 테스트")
    class Test2 {
        private final HandleGenerator handleGenerator = new HandleGeneratorImpl(32);
        private final Map<String, ByteArray> handlerMapper = new HashMap<>();
        private final Map<ByteArray, List<RegisteredCredential>> credentialMapper = new HashMap<>();
        private final PasskeyRepository passkeyRepository = new InMemoryPasskeyRepository(handlerMapper,
                credentialMapper);
        private final CredentialRepository credentialRepository = new InMemoryCredentialRepository(handlerMapper,
                credentialMapper);
        private final RelyingParty relyingParty = RelyingPartyFixture.create(credentialRepository);
        private final PasskeyRegistrationUseCase passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(
                passkeyRepository,
                relyingParty,
                handleGenerator
        );
        private final WebAuthnAuthenticator authenticator = Authenticators.yubikey5Nfc().build();
        private final Origin origin = new Origin("https", "sign.co.kr", -1, null);
        private final CredentialsContainer container = new CredentialsContainer(origin, List.of(authenticator));
        private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential;
        private PublicKeyCredentialCreationOptions options;

        @BeforeEach
        void setUp() {
            PasskeyRegistrationResult result = passkeyRegistrationUseCase.start(email);
            options = result.getOptions();
            credential = container.create(options);
        }

        @Test
        @DisplayName("패스키 정상 등록시 성공한다.")
        void test1() {
            PasskeyRegistrationResult result = passkeyRegistrationUseCase.finishInternal(options, credential, email);
            boolean actual = result.getStatus().isSuccess();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("해당 이메일의 패스키가 저장된다.")
        void test2() {
            passkeyRegistrationUseCase.finishInternal(options, credential, email);
            Optional<ByteArray> credential = passkeyRepository.findUserHandleByEmail(email);
            boolean actual = credential.isPresent();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("패스키를 중복으로 저장할 경우 실패한다.")
        void test3() {
            passkeyRegistrationUseCase.finishInternal(options, credential, email);

            PasskeyRegistrationResult result = passkeyRegistrationUseCase.finishInternal(options, credential, email);
            boolean actual = result.getFailReason().isEmpty();

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("다른 기기를 등록할 경우 성공한다.")
        void test4() {
            PasskeyRegistrationResult otherResult = passkeyRegistrationUseCase.start(email);
            PublicKeyCredentialCreationOptions otherOption = otherResult.getOptions();
            PublicKeyCredential<AuthenticatorAttestationResponse,
                    ClientRegistrationExtensionOutputs> otherCredential = container.create(otherOption);

            PasskeyRegistrationResult result = passkeyRegistrationUseCase.finishInternal(otherOption, otherCredential, email);
            boolean actual = result.getStatus().isSuccess();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("다른 Origin으로 등록할 경우 예외가 발생한다.")
        void test5() {
            Origin origin = new Origin("https", "example.com", -1, null);
            CredentialsContainer container = new CredentialsContainer(origin, List.of(authenticator));
            PasskeyRegistrationResult startResult = passkeyRegistrationUseCase.start(email);
            options = startResult.getOptions();
            credential = container.create(options);

            PasskeyRegistrationResult finishResult = passkeyRegistrationUseCase.finishInternal(options, credential, email);
            boolean actual = finishResult.getStatus().isSuccess();

            assertThat(actual).isFalse();
        }

        @Nested
        @DisplayName("개인정보 동의 여부 확인 테스트")
        class Test00 {

            @Test
            @DisplayName("개인정보 동의가 되어있으면 패스키 등록이 성공한다.")
            void test2() {
                PasskeyRegistrationResult result = passkeyRegistrationUseCase.finish(options, new PasskeyRegistrationRequest(true, credential), email);
                boolean actual = result.getStatus().isSuccess();

                assertThat(actual).isTrue();
            }

            @Test
            @DisplayName("개인정보 동의가 되어있지 않으면 패스키 등록이 실패한다.")
            void test3() {
                PasskeyRegistrationResult result = passkeyRegistrationUseCase.finish(options, new PasskeyRegistrationRequest(false, credential), email);
                boolean actual = result.getStatus().isSuccess();

                assertThat(actual).isFalse();
            }
        }

        @Nested
        @DisplayName("등록과 정보와 일치하지 않는 정보가 주어질 때")
        class WhenDifferentChallenge {

            private PublicKeyCredentialCreationOptions otherOptions;

            @BeforeEach
            void setUp() {
                PasskeyRegistrationResult otherResult = passkeyRegistrationUseCase.start(email);
                otherOptions = otherResult.getOptions();
            }

            @Test
            @DisplayName("전혀 다른 challenge로 패스키를 등록할 경우 예외가 발생한다.")
            void test1() {
                PasskeyRegistrationResult result = passkeyRegistrationUseCase.finishInternal(otherOptions, credential, email);
                boolean actual = result.getStatus().isSuccess();

                assertThat(actual).isFalse();
            }

            @Test
            @DisplayName("전혀 다른 credential로 패스키를 등록할 경우 예외가 발생한다.")
            void test2() {
                PublicKeyCredential<AuthenticatorAttestationResponse,
                        ClientRegistrationExtensionOutputs> otherCredential = container.create(otherOptions);

                PasskeyRegistrationResult result = passkeyRegistrationUseCase.finishInternal(options, otherCredential, email);
                boolean actual = result.getStatus().isSuccess();

                assertThat(actual).isFalse();
            }
        }
    }
}
