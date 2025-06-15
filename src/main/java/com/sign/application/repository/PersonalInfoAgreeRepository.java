package com.sign.application.repository;

public interface PersonalInfoAgreeRepository {
    void save(String email, boolean agree);
}
