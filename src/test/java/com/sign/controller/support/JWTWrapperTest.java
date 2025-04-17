package com.sign.controller.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.sign.controller.config.ResponseProtectorProperties;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JWTWrapperTest {

    private final Clock clock = Clock.systemDefaultZone();

    @Nested
    @DisplayName("wrapping 성공")
    class Test1 {
        private final ResponseProtectorProperties responseProtectorProperties = new ResponseProtectorProperties(
                "12345678901234567890123456789012",
                "footer",
                Duration.ofSeconds(30L));
        private final JWTWrapper JWTWrapper = new JWTWrapper(responseProtectorProperties, clock);
        private final String given = "1234";

        @Test
        @DisplayName("secretkey는 32자 이상이어야 한다.")
        void test1() {
            String encrypt = JWTWrapper.wrap(given);
            boolean actual = encrypt.isEmpty();
            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("wrapping 된 값은 원본 값과 달라야 한다.")
        void test2() {
            String encrypt = JWTWrapper.wrap(given);
            boolean actual = given.equals(encrypt);
            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("unwrap 성공")
    class Test3 {
        private final ResponseProtectorProperties properties = new ResponseProtectorProperties(
                "12345678901234567890123456789012",
                "footer",
                Duration.ofSeconds(30L));
        private final JWTWrapper JWTWrapper = new JWTWrapper(properties, clock);

        @Test
        @DisplayName("주어진 객체를 역직렬화하는데 성공한다.")
        void test1() {
            Integer expected = 1234;
            String encrypt = JWTWrapper.wrap(expected);
            Optional<Integer> unpack = JWTWrapper.unwrap(encrypt, Integer.class);
            Integer actual = unpack.get();

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("unwrap 실패")
    class Test4 {
        private final ResponseProtectorProperties properties = new ResponseProtectorProperties(
                "12345678901234567890123456789012",
                "footer",
                Duration.ofSeconds(30L));
        private final ResponseProtectorProperties differentPassword = new ResponseProtectorProperties(
                "otherSecretKeyotherSecretKeyotherSecretKey",
                "footer",
                Duration.ofSeconds(30L));
        private final JWTWrapper JWTWrapper = new JWTWrapper(properties, clock);

        @Test
        @DisplayName("unwrap에 실패하면 빈값을 반환한다.")
        void test1() {
            Optional<Integer> unpack = JWTWrapper.unwrap("", Integer.class);
            boolean actual = unpack.isEmpty();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("공유 키가 다르면 실패하면 빈값을 반환한다.")
        void test2() {
            Integer given = 1;
            JWTWrapper anotherJWTWrapper = new JWTWrapper(differentPassword, clock);
            String encrypt = anotherJWTWrapper.wrap(given);
            Optional<Integer> unpack = JWTWrapper.unwrap(encrypt, Integer.class);
            boolean actual = unpack.isEmpty();

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("기간이 만료된 토큰을 unwrap 하면 빈값을 반환한다.")
        void test3() {
            Integer given = 1;
            Instant fixedInstant = Instant.parse("1994-02-09T00:00:00Z");
            ZoneId zone = ZoneId.of("UTC");
            Clock fixedClock = Clock.fixed(fixedInstant, zone);
            JWTWrapper anotherJWTWrapper = new JWTWrapper(properties, fixedClock);
            String encrypt = anotherJWTWrapper.wrap(given);
            Optional<Integer> unpack = JWTWrapper.unwrap(encrypt, Integer.class);
            boolean actual = unpack.isEmpty();

            assertThat(actual).isTrue();
        }
    }
}