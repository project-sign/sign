package com.sign.dto;

import java.time.LocalDateTime;

public record EmailCertificationCode(String email, String certificationCode, LocalDateTime expiredAt) {
}
