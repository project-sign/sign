package com.sign.dto;

public enum Status {
    SUCCESS,
    FAIL;

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
