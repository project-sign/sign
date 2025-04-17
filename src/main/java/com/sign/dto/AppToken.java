package com.sign.dto;

import java.time.Instant;

public record AppToken(String serialized, Instant expired) {
}
