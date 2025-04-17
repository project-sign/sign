package com.sign.controller.support;

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
import org.springframework.stereotype.Component;

/**
 * 객체를 JWT 형식으로 래핑하고, 이를 다시 역직렬화할 수 있는 기능을 제공하는 컴포넌트입니다.
 *
 * <p>
 * JWT의 subject(sub) 필드에 직렬화된 객체를 저장하며, 만료 시간은 구성 파일에서 설정된 {@link ResponseProtectorProperties}를 기준으로 계산됩니다.
 * 복호화 시에는 서명 검증과 만료 시간을 체크하여 유효한 경우에만 값을 반환합니다.
 * </p>
 */
@RequiredArgsConstructor
@EnableConfigurationProperties(ResponseProtectorProperties.class)
@Component
public class JWTWrapper {

    private final ResponseProtectorProperties properties;
    private final Clock clock;
    private final JsonMapper mapper = mapper();

    /**
     * 주어진 객체를 JWT 문자열로 직렬화합니다.
     *
     * <p>
     * 직렬화된 객체는 {@code sub} 필드에 저장되며, {@code properties.expired()}에 설정된 시간만큼 유효한 JWT가 생성됩니다.
     * </p>
     *
     * @param value 직렬화할 객체
     * @return JWT 문자열. 직렬화 중 예외 발생 시 빈 문자열을 반환합니다.
     */
    public String wrap(Object value) {
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

    /**
     * JWT 문자열을 주어진 클래스 타입으로 역직렬화합니다.
     *
     * <p>
     * JWT의 유효성 검사(서명 및 만료 시간)를 수행하고, 조건을 만족하면 {@code Optional}로 역직렬화된 객체를 반환합니다.
     * 유효하지 않거나 예외가 발생한 경우 {@code Optional.empty()}를 반환합니다.
     * </p>
     *
     * @param protectedResponse JWT 문자열
     * @param clazz             역직렬화할 클래스 타입
     * @param <T>               반환 타입
     * @return 역직렬화된 객체를 포함한 {@code Optional}, 실패 시 {@code Optional.empty()}
     */
    public <T> Optional<T> unwrap(String protectedResponse, Class<T> clazz) {
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

    /**
     * 문자열로 직렬화된 데이터를 주어진 타입으로 역직렬화합니다.
     *
     * @param serialized 직렬화된 JSON 문자열
     * @param clazz      변환할 클래스 타입
     * @param <T>        변환 결과 타입
     * @return 역직렬화된 객체
     * @throws JsonProcessingException 역직렬화 실패 시 발생
     */
    private <T> T deserialize(String serialized, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(serialized, clazz);
    }

    /**
     * 현재 시각을 기준으로 만료 시각을 계산합니다.
     *
     * @return 만료 시각 {@link Instant}
     */
    private Instant calculateExpired() {
        return Instant.now(clock).plus(properties.expired());
    }

    /**
     * JWT 서명에 사용할 HMAC256 알고리즘을 반환합니다.
     *
     * @return {@link Algorithm} 인스턴스
     */
    private Algorithm key() {
        return Algorithm.HMAC256(properties.secretKey());
    }

    /**
     * JavaTimeModule이 등록된 {@link JsonMapper}를 반환합니다.
     *
     * @return 구성된 {@link JsonMapper}
     */
    private JsonMapper mapper() {
        JsonMapper mapper = new JsonMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}