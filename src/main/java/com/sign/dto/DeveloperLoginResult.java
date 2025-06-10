package com.sign.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeveloperLoginResult {

    private final Status status;

    private final String email;

    private final String failReason;

    public static DeveloperLoginResult success(String email) {
        return new DeveloperLoginResult(Status.SUCCESS, email, null);
    }

    public static DeveloperLoginResult failure(String failReason) {
        return new DeveloperLoginResult(Status.FAIL, null, failReason);
    }
}
