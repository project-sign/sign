package com.sign.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.sign.infrastructure.repository.HandleGeneratorImpl;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserIdentity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
    @DisplayName("Registration Option 테스트")
    class Test2 {
        private final RelyingParty relyingParty = Mockito.mock(RelyingParty.class);
        private final HandleGenerator handleGenerator = Mockito.mock(HandleGenerator.class);

        private final PasskeyRegistrationUseCase passkeyRegistrationUseCase = new PasskeyRegistrationUseCase(
                passkeyRepository,
                relyingParty,
                handleGenerator
        );

        @Test
        @DisplayName("relyingParty 등록 시작 시 residentKey를 요구해야 한다.")
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
}