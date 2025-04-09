package com.sign.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SecretKeyValidatorTest {

    @Test
    @DisplayName("키의 길이가 작으면 예외가 발생한다.")
    void test1() {
        assertThatThrownBy(() -> SecretKeyValidator.validate("shortKey"));
    }
}