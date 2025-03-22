package com.sign.dto;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailSendResult {

    private final Status status;

    private final String fromAddress;

    private final String toAddress;

    private final String subject;

    private final String failReason;

    public static EmailSendResult success(String fromAddress, String toAddress, String subject) {
        return new EmailSendResult(Status.SUCCESS, fromAddress, toAddress, subject, null);
    }

    public static EmailSendResult failure(String fromAddress, String toAddress, String subject, String failReason) {
        return new EmailSendResult(Status.FAIL, fromAddress, toAddress, subject, failReason);
    }

    private enum Status {
        SUCCESS,
        FAIL,
    }
}
