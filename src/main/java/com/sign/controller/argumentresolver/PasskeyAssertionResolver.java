package com.sign.controller.argumentresolver;

import com.sign.controller.CookieManager;
import com.sign.controller.ProtectedRequest;
import com.yubico.webauthn.AssertionRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class PasskeyAssertionResolver implements HandlerMethodArgumentResolver {
    private static final String ASSERTION_CHALLENGE_NAME = "assertion_challenge";

    private final ResponseProtector responseProtector;
    private final CookieManager cookieManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ProtectedRequest.class)
                && AssertionRequest.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String protectedOptions = cookieManager.findByName(request.getCookies(), ASSERTION_CHALLENGE_NAME);
        return responseProtector.unpack(protectedOptions, AssertionRequest.class)
                .orElseThrow(() -> new IllegalArgumentException("Assertion Challenge를 추출할 수 없습니다."));
    }
}
