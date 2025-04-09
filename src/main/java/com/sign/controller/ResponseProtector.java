package com.sign.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sign.controller.config.ResponseProtectorProperties;
import com.sign.dto.AppToken;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.paseto4j.commons.PasetoException;
import org.paseto4j.commons.SecretKey;
import org.paseto4j.commons.Version;
import org.paseto4j.version2.Paseto;
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
            return Paseto.encrypt(key(), payload, properties.footer());
        } catch (PasetoException | JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public <T> Optional<T> unpack(String protectedResponse, Class<T> clazz) {
        try {
            String payload = Paseto.decrypt(key(), protectedResponse, properties.footer());
            AppToken appToken = mapper.readValue(payload, AppToken.class);
            if (Instant.now(clock).isAfter(appToken.expired())) {
                return Optional.empty();
            }
            return Optional.of(deserialize(appToken.serialized(), clazz));
        } catch (PasetoException | JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private <T> T deserialize(String serialized, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(serialized, clazz);
    }

    private Instant calculateExpired() {
        return Instant.now(clock).plus(properties.expired());
    }

    private SecretKey key() {
        try {
            validateSecretKey();
            byte[] bytes = properties.plainPassword().getBytes(StandardCharsets.UTF_8);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new SecretKey(digest.digest(bytes), Version.V2);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonMapper mapper() {
        JsonMapper mapper = new JsonMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private void validateSecretKey() {
        if (properties.plainPassword() == null || properties.plainPassword().length() < 32) {
            throw new PasetoException("패스워드는 32자 이상이어야 합니다.");
        }
    }
}
