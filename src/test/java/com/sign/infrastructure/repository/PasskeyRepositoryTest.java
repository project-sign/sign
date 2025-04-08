package com.sign.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sign.application.repository.HandleGenerator;
import com.sign.application.repository.PasskeyRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import java.util.Optional;
import java.util.Set;
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
        @DisplayName("등록한 패스키를 조회할 수 있다.")
        void test1() {
            Optional<ByteArray> userHandleForUsername = passkeyRepository.getUserHandleForUsername(email);
            boolean actual = userHandleForUsername.isPresent();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("등록되지 않은 이메일은 조회할 수 없다.")
        void test2() {
            Optional<ByteArray> userHandleForUsername = passkeyRepository.getUserHandleForUsername("other@Test.com");
            boolean actual = userHandleForUsername.isPresent();

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("등록된 userHandle로도 이메일을 조회할 수 있어야 한다.")
        void test3() {
            String actual = passkeyRepository.getUsernameForUserHandle(userHandle).get();

            assertThat(actual).isEqualTo(email);
        }

        @Test
        @DisplayName("등록된 이메일로 등록된 기기들을 조회할 수 있어야 한다.")
        void test4() {
            Set<PublicKeyCredentialDescriptor> credentials = passkeyRepository.getCredentialIdsForUsername(email);
            boolean actual = credentials.isEmpty();

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("등록되지 않은 이메일로 조회 시 값이 비어있어야 한다.")
        void test5() {
            String other = "test@test.com";
            Set<PublicKeyCredentialDescriptor> credentials = passkeyRepository.getCredentialIdsForUsername(other);
            boolean actual = credentials.isEmpty();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("credentialId와 userHandle로 등록된 정보를 조회할 수 있어야 한다.")
        void test6() {
            RegisteredCredential actual = passkeyRepository.lookup(credential, userHandle).get();

            assertThat(actual).isEqualTo(registeredCredential);
        }

        @Test
        @DisplayName("credentialId와 연관된 등록된 정보를 조회할 수 있어야 한다.")
        void test7() {
            Set<RegisteredCredential> registeredCredentials = passkeyRepository.lookupAll(credential);
            boolean actual = registeredCredentials.isEmpty();

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("credential의 signcount를 업데이트 할 수 있어야 한다.")
        void test8() {
            Long expected = 3000L;
            passkeyRepository.updateSignatureCount(email, credential, expected);
            RegisteredCredential updatedRegisteredCredential = passkeyRepository.lookup(credential, userHandle).get();
            long actual = updatedRegisteredCredential.getSignatureCount();

            assertThat(actual).isEqualTo(expected);
        }
    }
}
