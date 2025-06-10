package com.sign.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sign.dto.DeveloperRegistrationRequest;
import com.sign.dto.DeveloperRegistrationResult;
import com.sign.dto.PasskeyAssertionResult;
import com.sign.dto.Status;
import com.sign.infrastructure.repository.DeveloperRepositoryImpl;
import com.yubico.webauthn.AssertionRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DeveloperRegistrationUseCaseTest {

    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    private static final LocalDateTime NOW = LocalDateTime.now(CLOCK);

    private final DeveloperRepositoryImpl repository = Mockito.mock(DeveloperRepositoryImpl.class);
    private final PasskeyAssertionUseCase passkeyAssertionUseCase = Mockito.mock(PasskeyAssertionUseCase.class);
    private final DeveloperRegistrationUseCase developerRegistrationUseCase;
    private final AssertionRequest options = Mockito.mock(AssertionRequest.class);
    private final DeveloperRegistrationRequest request = Mockito.mock(DeveloperRegistrationRequest.class);


    DeveloperRegistrationUseCaseTest() {
        developerRegistrationUseCase = new DeveloperRegistrationUseCase(repository, passkeyAssertionUseCase, CLOCK);
    }

    @Nested
    @DisplayName("registerDeveloper 테스트")
    class Test1 {

        @Nested
        @DisplayName("패스키 인증에 실패한다면")
        class WhenPasskeyAssertionFailed {

            @BeforeEach
            void mock() {
                when(passkeyAssertionUseCase.finish(any(), any()))
                        .thenReturn(PasskeyAssertionResult.failure("패스키 인증 실패"));
            }

            @Test
            @DisplayName("개발자 가입을 진행할 수 없다.")
            void test1() {
                Status expected = Status.FAIL;

                DeveloperRegistrationResult result = developerRegistrationUseCase.registerDeveloper(
                        options, request);
                Status actual = result.getStatus();

                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        @DisplayName("동의를 하지 않으면")
        class WhenNotAgree {

            @BeforeEach
            void mock() {
                when(passkeyAssertionUseCase.finish(any(), any()))
                        .thenReturn(PasskeyAssertionResult.success("test@sign.com"));
                when(request.agree()).thenReturn(false);
            }

            @Test
            @DisplayName("개발자 가입을 진행할 수 없다.")
            void test1() {
                Status expected = Status.FAIL;

                DeveloperRegistrationResult result = developerRegistrationUseCase.registerDeveloper(
                        options, request);
                Status actual = result.getStatus();

                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        @DisplayName("패스키 인증이 성공하고, 동의 한다면")
        class WhenAgree {

            @BeforeEach
            void mock() {
                when(passkeyAssertionUseCase.finish(any(), any()))
                        .thenReturn(PasskeyAssertionResult.success("test@sign.com"));
                when(request.agree()).thenReturn(true);
                when(request.email()).thenReturn("test@sign.com");
                developerRegistrationUseCase.registerDeveloper(options, request);
            }


            @Test
            @DisplayName("데이터베이스에 개발자 회원과 동의 날짜가 저장된다.")
            void test2() {
                verify(repository).save(
                        eq("test@sign.com"),
                        eq(LocalDateTime.now(CLOCK))
                );
            }
        }
    }
}
