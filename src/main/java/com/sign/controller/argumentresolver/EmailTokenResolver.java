package com.sign.controller.argumentresolver;

import com.sign.controller.support.CookieManager;
import com.sign.controller.support.JWTWrapped;
import com.sign.controller.support.JWTWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class EmailTokenResolver implements HandlerMethodArgumentResolver {

    private static final String EMAIL_TOKEN_NAME = "email_token";

    private final JWTWrapper JWTWrapper;
    private final CookieManager cookieManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JWTWrapped.class)
                && String.class.isAssignableFrom(parameter.getParameterType());

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        // email_token=의 쿠키 값을 추출합니다.
        String protectedEmailToken = cookieManager.findByName(request.getCookies(), EMAIL_TOKEN_NAME);
        // jwt를 두번째 파라미터의 class 인스턴스로 변환합니다. 즉 String객체를 반환하게 됩니다.
        return JWTWrapper.unwrap(protectedEmailToken, String.class)
                .orElseThrow(() -> new IllegalArgumentException("이메일을 추출할 수 없습니다."));
    }
}
