package com.sign.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.sign.controller.config.ResponseProtectorProperties;
import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ResponseProtectorTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Nested
    @DisplayName("ResponseProtector 성공")
    class Test1 {
        private final ResponseProtectorProperties responseProtectorProperties = new ResponseProtectorProperties(
                "12345678901234567890123456789012",
                "footer",
                Duration.ofSeconds(30L));
        private final ResponseProtector responseProtector = new ResponseProtector(responseProtectorProperties, clock);
        private final String given = "1234";

        @Test
        @DisplayName("secretkey는 32byte 이상이어야 한다.")
        void test1() {
            String encrypt = responseProtector.encrypt(given);
            boolean actual = encrypt.isEmpty();
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("암호화 된 값은 원본 값과 달라야 한다.")
        void test2() {
            String encrypt = responseProtector.encrypt(given);
            boolean actual = given.equals(encrypt);
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("ResponseProtector 실패")
    class Test2 {
        private final ResponseProtectorProperties responseProtectorProperties = new ResponseProtectorProperties(
                "secret",
                "footer",
                Duration.ofSeconds(30L));
        private final ResponseProtector responseProtector = new ResponseProtector(responseProtectorProperties, clock);

        @Test
        @DisplayName("encrypt가 되지 않았다면 빈 값을 반환한다.")
        void test1() {
            String encrypt = responseProtector.encrypt("1234");
            boolean actual = encrypt.isEmpty();
            assertThat(actual).isTrue();
        }
    }
}