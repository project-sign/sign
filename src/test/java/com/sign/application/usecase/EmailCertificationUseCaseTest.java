package com.sign.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sign.application.repository.EmailCertificationLogger;
import com.sign.application.repository.EmailCertificationRepository;
import com.sign.application.repository.EmailSender;
import com.sign.application.repository.RandomCodeGenerator;
import com.sign.application.usecase.config.EmailCertificationProperties;
import com.sign.dto.EmailCertificationCode;
import com.sign.dto.EmailCertificationRequest;
import com.sign.dto.EmailSendResult;
import com.sign.dto.EmailValidationRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmailCertificationUseCaseTest {

    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    private static final LocalDateTime NOW = LocalDateTime.now(CLOCK);

    private final EmailCertificationRepository emailCertificationRepository = Mockito.mock(
            EmailCertificationRepository.class
    );
    private final EmailSender emailSender = Mockito.mock(EmailSender.class);
    private final RandomCodeGenerator codeGenerator = Mockito.mock(RandomCodeGenerator.class);
    private final EmailCertificationLogger emailCertificationLogger = Mockito.mock(EmailCertificationLogger.class);

    private final EmailCertificationProperties emailCertificationProperties = new EmailCertificationProperties(
            300, 60, "sign@sign.co.kr"
    );

    private final EmailCertificationUseCase emailCertificationUseCase = new EmailCertificationUseCase(
            emailCertificationRepository,
            emailSender,
            codeGenerator,
            emailCertificationLogger,
            emailCertificationProperties,
            CLOCK
    );

    @Nested
    @DisplayName("sendCertification 테스트")
    class Test1 {

        @DisplayName("이메일 규격에 맞지 않는 주소로 보낸다면")
        void test() {
            assertThatThrownBy(
                    () -> emailCertificationUseCase.sendCertification(new EmailCertificationRequest("unValidAddress"))
            ).isInstanceOf(IllegalAccessError.class).hasMessage("잘못된 이메일 주소입니다.");
        }

        @Nested
        @DisplayName("인증 전송 로그가 없다면")
        class WhenLogNotFound {
            @BeforeEach
            void mock() {
                when(emailCertificationLogger.lastCreatedAtFor(any())).thenReturn(Optional.empty());
                emailCertificationUseCase.sendCertification(new EmailCertificationRequest("test@test.com"));
            }

            @Test
            @DisplayName("코드가 생성된다.")
            void test1() {
                verify(codeGenerator).generate(6);
            }

            @Test
            @DisplayName("인증 정보가 저장된다.")
            void test2() {
                verify(emailCertificationRepository).save(
                        new EmailCertificationCode("test@test.com", any(), NOW.plusMinutes(5))
                );
            }

            @Test
            @DisplayName("인증 생성 로그가 생성된다.")
            void test3() {
                verify(emailCertificationLogger).logCertification(
                        new EmailCertificationCode("test@test.com", any(), NOW.plusMinutes(5))
                );
            }

            @Test
            @DisplayName("인증메일이 전송된다.")
            void test4() {
                verify(emailSender).send(
                        eq(emailCertificationProperties.mailHost()),
                        eq("test@test.com"),
                        any(),
                        any()
                );
            }
        }

        @Nested
        @DisplayName("인증 전송 로그가 있다면")
        class WhenLogFound {
            @Nested
            @DisplayName("인증 전송 로그가 1분 이내에 있다면")
            class WhenLogFound1 {
                @BeforeEach
                void mock() {
                    when(emailCertificationLogger.lastCreatedAtFor(any())).thenReturn(
                            Optional.of(NOW.minusSeconds(59))
                    );
                    emailCertificationUseCase.sendCertification(new EmailCertificationRequest("test@test.com"));
                }

                @Test
                @DisplayName("인증 메일이 전송되지 않는다.")
                void test1() {
                    assertThat(
                            emailCertificationUseCase.sendCertification(new EmailCertificationRequest("test@test.com"))
                    ).isEqualTo(
                            EmailSendResult.failure(
                                    emailCertificationProperties.mailHost(),
                                    "test@test.com",
                                    "Sign 인증 번호",
                                    "아직 인증 메일을 보낼 수 없습니다."
                            )
                    );
                }
            }

            @Nested
            @DisplayName("인증 전송 로그가 1분 이내에 없다면")
            class WhenLogFound2 {

                @BeforeEach
                void mock() {
                    when(emailCertificationLogger.lastCreatedAtFor(any())).thenReturn(
                            Optional.of(NOW.minusSeconds(60))
                    );
                    emailCertificationUseCase.sendCertification(new EmailCertificationRequest("test@test.com"));
                }

                @Test
                @DisplayName("코드가 생성된다.")
                void test1() {
                    verify(codeGenerator).generate(6);
                }

                @Test
                @DisplayName("인증 정보가 저장된다.")
                void test2() {
                    verify(emailCertificationRepository).save(
                            new EmailCertificationCode("test@test.com", any(), NOW.plusMinutes(5))
                    );
                }

                @Test
                @DisplayName("인증 생성 로그가 생성된다.")
                void test3() {
                    verify(emailCertificationLogger).logCertification(
                            new EmailCertificationCode("test@test.com", any(), NOW.plusMinutes(5))
                    );
                }

                @Test
                @DisplayName("인증메일이 전송된다.")
                void test4() {
                    verify(emailSender).send(
                            eq(emailCertificationProperties.mailHost()),
                            eq("test@test.com"),
                            any(),
                            any()
                    );
                }
            }
        }
    }

    @Nested
    @DisplayName("validateCertification 테스트")
    class Test2 {

        @Test
        @DisplayName("이메일에 해당하는 인증코드가 없을 때 false 를 반환한다.")
        void test1() {
            when(emailCertificationRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
            boolean result = emailCertificationUseCase.validateCertification(
                    new EmailValidationRequest("test@test.com", "123456")
            );
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("이메일에 해당하는 인증코드와 입력이 다를 때 false 를 반환한다.")
        void test2() {
            when(emailCertificationRepository.findByEmail("test@test.com")).thenReturn(
                    Optional.of(new EmailCertificationCode("test@test.com", "111111", NOW.plusMinutes(5)))
            );
            boolean result = emailCertificationUseCase.validateCertification(
                    new EmailValidationRequest("test@test.com", "123456")
            );
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("이메일에 해당하는 인증코드와 입력이 같을 때 true 를 반환한다.")
        void test3() {
            when(emailCertificationRepository.findByEmail("test@test.com")).thenReturn(
                    Optional.of(new EmailCertificationCode("test@test.com", "123456", NOW.plusMinutes(5)))
            );
            boolean result = emailCertificationUseCase.validateCertification(
                    new EmailValidationRequest("test@test.com", "123456")
            );
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("이메일에 해당하는 인증 코드가 이미 만료되었을 때 false 를 반환한다.")
        void test4() {
            when(emailCertificationRepository.findByEmail("test@test.com")).thenReturn(
                    Optional.of(new EmailCertificationCode("test@test.com", "123456", NOW.minusSeconds(1)))
            );
            boolean result = emailCertificationUseCase.validateCertification(
                    new EmailValidationRequest("test@test.com", "123456")
            );
            assertThat(result).isFalse();
        }
    }
}