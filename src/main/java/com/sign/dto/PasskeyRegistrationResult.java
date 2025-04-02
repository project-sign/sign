package com.sign.dto;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PasskeyRegistrationResult {

    private final Status status;

    private final PublicKeyCredentialCreationOptions options;

    private final String failReason;

    public static PasskeyRegistrationResult success(PublicKeyCredentialCreationOptions options) {
        return new PasskeyRegistrationResult(Status.SUCCESS, options, null);
    }

    public static PasskeyRegistrationResult success() {
        return new PasskeyRegistrationResult(Status.SUCCESS, null, null);
    }

    public static PasskeyRegistrationResult failure(String failReason) {
        return new PasskeyRegistrationResult(Status.FAIL, null, failReason);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    enum Status {
        SUCCESS,
        FAIL
    }
}
