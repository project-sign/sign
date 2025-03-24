package com.sign.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

public record APIResponse<T>(@JsonIgnore HttpStatus status, String message, T data) {

    public APIResponse(String message, T data) {
        this(HttpStatus.OK, message, data);
    }
}
