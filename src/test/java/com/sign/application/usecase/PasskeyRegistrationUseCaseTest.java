package com.sign.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.sign.infrastructure.repository.HandleGeneratorImpl;
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
import de.adesso.softauthn.Authenticators;
import de.adesso.softauthn.CredentialsContainer;
import de.adesso.softauthn.Origin;
import de.adesso.softauthn.authenticator.WebAuthnAuthenticator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

class PasskeyRegistrationUseCaseTest {

    private final String email = "passkey@sign.co.kr";
    private final PasskeyRepository passkeyRepository = Mockito.mock(PasskeyRepository.class);

    @Nested
    @DisplayName("Registration Value 테스트")
    class Test1 {
        private final RelyingParty relyingParty = RelyingPartyFixture.create(passkeyRepository);
        private final HandleGenerator handleGenerator = new HandleGeneratorImpl(32);
        private final PasskeyRegistrationUseCase passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(
                passkeyRepository,
                relyingParty,
                handleGenerator
        );

        @Test
        @DisplayName("같은 이메일을 전달하면 서로 다른 challenge값이 생성된다.")
        void test1() {
            PublicKeyCredentialCreationOptions options1 = passkeyRegistrationUseCase.start(email);
            PublicKeyCredentialCreationOptions options2 = passkeyRegistrationUseCase.start(email);

            ByteArray challenge1 = options1.getChallenge();
            ByteArray challenge2 = options2.getChallenge();

            assertThat(challenge1.getBase64Url()).isNotEqualTo(challenge2.getBase64Url());
        }
    }

    @Nested
    @DisplayName("Registration Start 시")
    class WhenRegistrationStart {
        private final RelyingParty relyingParty = Mockito.mock(RelyingParty.class);
        private final HandleGenerator handleGenerator = Mockito.mock(HandleGenerator.class);

        private final PasskeyRegistrationUseCase passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(
                passkeyRepository,
                relyingParty,
                handleGenerator
        );

        @Test
        @DisplayName("residentKey를 요구해야 한다.")
        void test1() {
            ByteArray userHandle = new ByteArray(new byte[]{0x01, 0x02, 0x03, 0x04});
            when(handleGenerator.generateHandle()).thenReturn(userHandle);
            passkeyRegistrationUseCase.start(email);
            AuthenticatorSelectionCriteria authSelection = AuthenticatorSelectionCriteria.builder()
                    .residentKey(ResidentKeyRequirement.REQUIRED)
                    .build();

            verify(relyingParty).startRegistration(
                    StartRegistrationOptions.builder()
                            .user(UserIdentity.builder()
                                    .name(email)
                                    .displayName(email)
                                    .id(userHandle)
                                    .build())
                            .authenticatorSelection(authSelection)
                            .build());
        }
    }

    @Nested
    @DisplayName("Registration Finish 테스트")
    class Test2 {
        private final RelyingParty relyingParty = RelyingPartyFixture.create(passkeyRepository);
        private final HandleGenerator handleGenerator = new HandleGeneratorImpl(32);
        private final WebAuthnAuthenticator authenticator = Authenticators.yubikey5Nfc().build();
        private final PasskeyRegistrationUseCase passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(
                passkeyRepository,
                relyingParty,
                handleGenerator
        );

        @Nested
        @DisplayName("올바른 Origin을 설정하고 요청하면")
        class WhenCorrectOrigin {
            Origin origin = new Origin("https", "sign.co.kr", -1, null);
            CredentialsContainer container = new CredentialsContainer(origin, List.of(authenticator));
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential;
            PublicKeyCredentialCreationOptions options;

            @BeforeEach
            void setUp() {
                options = passkeyRegistrationUseCase.start(email);
                credential = container.create(options);
            }

            @Test
            @DisplayName("예외가 발생하지 않는다.")
            void test1() {
                assertThatCode(() -> passkeyRegistrationUseCase.finish(options, credential, email))
                        .doesNotThrowAnyException();
            }

            @Test
            @DisplayName("해당 이메일의 패스키가 저장된다.")
            void test2() {
                passkeyRegistrationUseCase.finish(options, credential, email);
                verify(passkeyRepository).save(ArgumentMatchers.eq(email), any(RegisteredCredential.class));
            }
        }

        @Nested
        @DisplayName("올바른 Origin을 설정하지 않으면")
        class WhenInCorrectOrigin {
            Origin origin = new Origin("https", "example.com", -1, null);
            CredentialsContainer container = new CredentialsContainer(origin, List.of(authenticator));
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential;
            PublicKeyCredentialCreationOptions options;

            @BeforeEach
            void setUp() {
                options = passkeyRegistrationUseCase.start(email);
                credential = container.create(options);
            }

            @Test
            @DisplayName("예외가 발생한다.")
            void test1() {
                assertThatThrownBy(() -> passkeyRegistrationUseCase.finish(options, credential, email));
            }
        }
    }
}