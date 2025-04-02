package com.sign.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PasskeyAssertionResult {

    private final Status status;

    private final String email;

    private final String failReason;

    public static PasskeyAssertionResult success(String email) {
        return new PasskeyAssertionResult(Status.SUCCESS, email, null);
    }

    public static PasskeyAssertionResult failure(String failReason) {
        return new PasskeyAssertionResult(Status.FAIL, null, failReason);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    enum Status {
        SUCCESS,
        FAIL
    }
}
