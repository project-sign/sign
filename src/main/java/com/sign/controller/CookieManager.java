package com.sign.controller;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * 쿠키를 생성하고 조회하는 기능을 제공하는 컴포넌트 클래스입니다.
 *
 * <p>
 * 이 클래스는 JWT나 기타 토큰 정보를 {@code HttpOnly} 속성을 가진 쿠키로 생성하거나,
 * 클라이언트로부터 전달받은 쿠키 배열에서 특정 이름의 쿠키 값을 조회하는 유틸리티 메서드를 제공합니다.
 * </p>
 */
@Component
public class CookieManager {

    /**
     * 주어진 이름과 값을 바탕으로 {@code HttpOnly} 쿠키를 생성합니다.
     *
     * <p>
     * 생성되는 쿠키의 최종 형태는 {@code name=value; HttpOnly} 형태입니다.
     * </p>
     *
     * @param name  쿠키 이름
     * @param value 쿠키에 저장할 값
     * @return {@link ResponseCookie} 객체
     */
    public ResponseCookie provide(String name, String value) {
        return ResponseCookie.from(name)
                .value(value)
                .httpOnly(true)
                .build();
    }

    /**
     * 전달받은 쿠키 배열에서 특정 이름의 쿠키 값을 찾아 반환합니다.
     *
     * <p>
     * 쿠키가 존재하지 않거나 해당 이름의 쿠키가 없을 경우 빈 문자열 {@code ""}을 반환합니다.
     * </p>
     *
     * @param cookies 클라이언트로부터 전달받은 쿠키 배열
     * @param name    찾고자 하는 쿠키의 이름
     * @return 쿠키 값 또는 빈 문자열
     */
    public String findByName(Cookie[] cookies, String name) {
        return Arrays.stream(cookies)
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }
}
