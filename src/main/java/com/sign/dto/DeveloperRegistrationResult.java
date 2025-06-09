package com.sign.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeveloperRegistrationResult {

    private final Status status;

    private final String failReason;

    public static DeveloperRegistrationResult success() {
        return new DeveloperRegistrationResult(Status.SUCCESS, null);
    }

    public static DeveloperRegistrationResult failure(String failReason) {
        return new DeveloperRegistrationResult(Status.FAIL, failReason);
    }
}
