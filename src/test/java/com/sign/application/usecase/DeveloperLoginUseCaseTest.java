package com.sign.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sign.domain.Developer;
import com.sign.dto.DeveloperLoginRequest;
import com.sign.dto.DeveloperLoginResult;
import com.sign.dto.PasskeyAssertionResult;
import com.sign.dto.Status;
import com.sign.infrastructure.repository.DeveloperRepositoryImpl;
import com.yubico.webauthn.AssertionRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DeveloperLoginUseCaseTest {

    private final DeveloperRepositoryImpl repository = Mockito.mock(DeveloperRepositoryImpl.class);
    private final PasskeyAssertionUseCase passkeyAssertionUseCase = Mockito.mock(PasskeyAssertionUseCase.class);
    private final DeveloperLoginUseCase developerLoginUseCase;
    private final AssertionRequest options = Mockito.mock(AssertionRequest.class);
    private final DeveloperLoginRequest request = Mockito.mock(DeveloperLoginRequest.class);


    DeveloperLoginUseCaseTest() {
        developerLoginUseCase = new DeveloperLoginUseCase(repository, passkeyAssertionUseCase);
    }

    @Nested
    @DisplayName("login 테스트")
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
            @DisplayName("로그인 할 수 없다.")
            void test1() {
                Status expected = Status.FAIL;

                DeveloperLoginResult result = developerLoginUseCase.login(options, request);
                Status actual = result.getStatus();

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("패스키 실패 오류를 그대로 반환하지 않는다.")
            void test2() {

                DeveloperLoginResult result = developerLoginUseCase.login(options, request);
                String actual = result.getFailReason();

                assertThat(actual).isNotEqualTo("패스키 인증 실패");
            }
        }

        @Nested
        @DisplayName("개발자 회원이 존재하지 않으면")
        class WhenDeveloperNotFound {

            @BeforeEach
            void mock() {
                when(passkeyAssertionUseCase.finish(any(), any()))
                        .thenReturn(PasskeyAssertionResult.success("test@sign.com"));
                when(repository.findByEmail(any())).thenReturn(Optional.empty());
            }

            @Test
            @DisplayName("로그인 할 수 없다.")
            void test1() {
                Status expected = Status.FAIL;

                DeveloperLoginResult result = developerLoginUseCase.login(options, request);
                Status actual = result.getStatus();

                assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        @DisplayName("패스키 인증이 성공하고, 동의 한다면")
        class WhenDeveloperFound {

            @BeforeEach
            void mock() {
                when(passkeyAssertionUseCase.finish(any(), any()))
                        .thenReturn(PasskeyAssertionResult.success("test@sign.com"));
                when(repository.findByEmail(any())).thenReturn(Optional.of(new Developer("test@sign.com")));
            }


            @Test
            @DisplayName("로그인한 개발자 이메일을 반환한다.")
            void test1() {
                String expected = "test@sign.com";

                DeveloperLoginResult result = developerLoginUseCase.login(options, request);
                String actual = result.getEmail();

                assertThat(actual).isEqualTo(expected);
            }
        }
    }
}
