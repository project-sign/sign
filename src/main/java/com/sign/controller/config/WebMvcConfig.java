package com.sign.controller.config;

import com.sign.controller.CookieManager;
import com.sign.controller.argumentresolver.EmailTokenResolver;
import com.sign.controller.argumentresolver.PassKeyRegistrationResolver;
import com.sign.controller.argumentresolver.PasskeyAssertionResolver;
import com.sign.controller.argumentresolver.ResponseProtector;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ResponseProtector protector;
    private final CookieManager cookieManager;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new EmailTokenResolver(protector, cookieManager));
        resolvers.add(new PassKeyRegistrationResolver(protector, cookieManager));
        resolvers.add(new PasskeyAssertionResolver(protector, cookieManager));
    }
}
