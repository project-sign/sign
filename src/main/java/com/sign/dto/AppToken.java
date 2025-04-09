package com.sign.dto;

import java.time.Instant;

public record AppToken(Object value, Instant expired) {
}
