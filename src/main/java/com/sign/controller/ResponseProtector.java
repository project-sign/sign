package com.sign.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sign.controller.config.ResponseProtectorProperties;
import com.sign.dto.AppToken;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@EnableConfigurationProperties(ResponseProtectorProperties.class)
@Service
public class ResponseProtector {

    private final ResponseProtectorProperties properties;
    private final Clock clock;
    private final JsonMapper mapper = mapper();

    public String encrypt(Object value) {
        try {
            AppToken token = new AppToken(mapper.writeValueAsString(value), calculateExpired());
            String payload = mapper.writeValueAsString(token);
            return JWT.create()
                    .withSubject(payload)
                    .sign(key());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public <T> Optional<T> unpack(String protectedResponse, Class<T> clazz) {
        try {
            JWTVerifier verifier = JWT.require(key()).build();
            DecodedJWT payload = verifier.verify(protectedResponse);
            AppToken appToken = mapper.readValue(payload.getSubject(), AppToken.class);
            if (Instant.now(clock).isAfter(appToken.expired())) {
                return Optional.empty();
            }
            return Optional.of(deserialize(appToken.serialized(), clazz));
        } catch (JWTVerificationException | JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private <T> T deserialize(String serialized, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(serialized, clazz);
    }

    private Instant calculateExpired() {
        return Instant.now(clock).plus(properties.expired());
    }

    private Algorithm key() {
        return Algorithm.HMAC256(properties.secretKey());
    }

    private JsonMapper mapper() {
        JsonMapper mapper = new JsonMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
