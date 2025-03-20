package com.sign.dto;

public record APIResponse<T>(String message, T data) {
}
