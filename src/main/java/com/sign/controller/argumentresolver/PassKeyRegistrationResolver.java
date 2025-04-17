package com.sign.controller.argumentresolver;

import com.sign.controller.CookieManager;
import com.sign.controller.support.JWTWrapped;
import com.sign.controller.support.JWTWrapper;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class PassKeyRegistrationResolver implements HandlerMethodArgumentResolver {

    private static final String REGISTRATION_CHALLENGE_NAME = "registration_challenge";

    private final JWTWrapper JWTWrapper;
    private final CookieManager cookieManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JWTWrapped.class)
                && PublicKeyCredentialCreationOptions.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String protectedOptions = cookieManager.findByName(request.getCookies(), REGISTRATION_CHALLENGE_NAME);
        return JWTWrapper.unwrap(protectedOptions, PublicKeyCredentialCreationOptions.class)
                .orElseThrow(() -> new IllegalArgumentException("Registration Challenge를 추출할 수 없습니다."));
    }
}
