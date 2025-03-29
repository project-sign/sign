package com.sign.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EmailValidator 테스트")
class EmailValidatorTest {

    @Nested
    @DisplayName("Test1: 정상 검사")
    class Test1 {
        @Test
        @DisplayName("유효한 이메일 주소 검증 - 일반 이메일")
        void validEmailTest() {
            // 정상 동작하는 케이스: 유효한 이메일 주소는 예외가 발생하지 않아야 함
            assertDoesNotThrow(() -> EmailValidator.validateEmailAddress("user@example.com"));
        }

        @Test
        @DisplayName("유효한 이메일 주소 검증 - 숫자 포함 이메일")
        void validEmailTestWithNumbers() {
            assertDoesNotThrow(() -> EmailValidator.validateEmailAddress("user123@test.co.kr"));
        }
    }

    @Nested
    @DisplayName("Test2: 비정상 검사")
    class Test2 {
        @Test
        @DisplayName("null 값 검증")
        void nullEmailTest() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> EmailValidator.validateEmailAddress(null));
            assertEquals("잘못된 이메일 주소입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("잘못된 형식의 이메일 주소 검증")
        void invalidEmailTest() {
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> EmailValidator.validateEmailAddress("invalid-email"));
            assertEquals("잘못된 이메일 주소입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("앞이나 뒤에 점이 있는 이메일 주소 검증")
        void invalidEmailDotTest() {
            // 앞에 점이 있는 이메일
            Exception exception1 = assertThrows(IllegalArgumentException.class,
                    () -> EmailValidator.validateEmailAddress(".user@example.com"));
            assertEquals("잘못된 이메일 주소입니다.", exception1.getMessage());

            // 뒤에 점이 있는 이메일
            Exception exception2 = assertThrows(IllegalArgumentException.class,
                    () -> EmailValidator.validateEmailAddress("user.@example.com"));
            assertEquals("잘못된 이메일 주소입니다.", exception2.getMessage());
        }
    }
}