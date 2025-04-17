package com.sign.infrastructure.yubico.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.sign.application.repository.HandleGenerator;
import com.sign.infrastructure.repository.HandleGeneratorImpl;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public abstract class CredentialRepositoryTest {

    private final String email = "test@sign.co.kr";
    private final HandleGenerator handleGenerator = new HandleGeneratorImpl(32);
    private final ByteArray credential = handleGenerator.generateHandle();
    private final ByteArray userHandle = handleGenerator.generateHandle();
    private final ByteArray publicKeyCose = handleGenerator.generateHandle();
    protected CredentialRepository credentialRepository;
    private RegisteredCredential registeredCredential;

    abstract public void cleanUp();

    abstract public void saveData(String email, RegisteredCredential registeredCredential);

    @BeforeEach
    void setUp() {
        // initial data
        cleanUp();
        registeredCredential = RegisteredCredential.builder()
                .credentialId(credential)
                .userHandle(userHandle)
                .publicKeyCose(publicKeyCose)
                .build();
        saveData(email, registeredCredential);
    }

    @Test
    @DisplayName("등록한 패스키를 조회할 수 있다.")
    void test1() {
        Optional<ByteArray> userHandleForUsername = credentialRepository.getUserHandleForUsername(email);
        boolean actual = userHandleForUsername.isPresent();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("등록되지 않은 이메일은 조회할 수 없다.")
    void test2() {
        Optional<ByteArray> userHandleForUsername = credentialRepository.getUserHandleForUsername("other@Test.com");
        boolean actual = userHandleForUsername.isPresent();

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("등록된 userHandle로도 이메일을 조회할 수 있어야 한다.")
    void test3() {
        String actual = credentialRepository.getUsernameForUserHandle(userHandle).get();

        assertThat(actual).isEqualTo(email);
    }

    @Test
    @DisplayName("등록된 이메일로 등록된 기기들을 조회할 수 있어야 한다.")
    void test4() {
        Set<PublicKeyCredentialDescriptor> credentials = credentialRepository.getCredentialIdsForUsername(email);
        boolean actual = credentials.isEmpty();

        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("등록되지 않은 이메일로 조회 시 값이 비어있어야 한다.")
    void test5() {
        String other = "test@test.com";
        Set<PublicKeyCredentialDescriptor> credentials = credentialRepository.getCredentialIdsForUsername(other);
        boolean actual = credentials.isEmpty();

        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("credentialId와 userHandle로 등록된 정보를 조회할 수 있어야 한다.")
    void test6() {
        RegisteredCredential actual = credentialRepository.lookup(credential, userHandle).get();

        assertThat(actual).isEqualTo(registeredCredential);
    }

    @Test
    @DisplayName("credentialId와 연관된 등록된 정보를 조회할 수 있어야 한다.")
    void test7() {
        Set<RegisteredCredential> registeredCredentials = credentialRepository.lookupAll(credential);
        boolean actual = registeredCredentials.isEmpty();

        assertThat(actual).isFalse();
    }
}
