package com.sign.domain;

public class SecretKeyValidator {
    private static final int SECRET_KEY_LENGTH = 32;

    public static void validate(String secretKey) {
        if (secretKey == null || secretKey.length() < SECRET_KEY_LENGTH) {
            throw new IllegalArgumentException("Secretkey는 %d자 이상이어야 합니다.".formatted(SECRET_KEY_LENGTH));
        }
    }
}
