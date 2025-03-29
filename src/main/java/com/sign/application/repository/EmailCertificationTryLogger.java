package com.sign.application.repository;

public interface EmailCertificationTryLogger {

    void saveTryCount(String email, int tryCount);

    int findCertificationTryCount(String email);
}
