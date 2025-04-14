package com.sign.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

abstract class PasskeyRepositoryTest {

    private final String email = "test@sign.co.kr";
    private final HandleGenerator handleGenerator = new HandleGeneratorImpl(32);
    private final ByteArray credential = handleGenerator.generateHandle();
    private final ByteArray userHandle = handleGenerator.generateHandle();
    private final ByteArray publicKeyCose = handleGenerator.generateHandle();
    protected PasskeyRepository passkeyRepository;

    abstract public void cleanUp();

    abstract public RegisteredCredential findRegisteredCredential(ByteArray credential, ByteArray userHandle);

    @Nested
    @DisplayName("생성 테스트")
    class Test1 {

        @Test
        @DisplayName("등록한 패스키를 저장할 수 있다.")
        void test1() {
            RegisteredCredential registeredCredential = RegisteredCredential.builder()
                    .credentialId(credential)
                    .userHandle(userHandle)
                    .publicKeyCose(publicKeyCose)
                    .build();
            passkeyRepository.save(email, registeredCredential);
        }
    }

    @Nested
    @DisplayName("조회 테스트")
    class Test2 {
        private RegisteredCredential registeredCredential;

        @BeforeEach
        void setUp() {
            cleanUp();
            registeredCredential = RegisteredCredential.builder()
                    .credentialId(credential)
                    .userHandle(userHandle)
                    .publicKeyCose(publicKeyCose)
                    .build();
            passkeyRepository.save(email, registeredCredential);
        }

        @Test
        @DisplayName("email을 통해 userHandle을 얻을 수 있어야 한다.")
        void test2() {
            ByteArray actual = passkeyRepository.findUserHandleByEmail(email).get();
            assertThat(actual).isEqualTo(userHandle);
        }

        @Test
        @DisplayName("credential의 signcount를 업데이트 할 수 있어야 한다.")
        void test3() {
            Long expected = 3000L;
            passkeyRepository.updateSignatureCount(email, credential, expected);
            RegisteredCredential updatedRegisteredCredential = findRegisteredCredential(credential, userHandle);
            long actual = updatedRegisteredCredential.getSignatureCount();

            assertThat(actual).isEqualTo(expected);
        }
    }
}
