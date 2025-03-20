package com.sign.dto;

public record EmailCertificationSendResult(String to, int reSendAbleAt, int expiredAt) {
}
