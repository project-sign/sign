package com.sign.application.repository;

import java.time.LocalDateTime;

public interface CertificationLogger {

    LocalDateTime lastCreatedAtFor(String email);
}
