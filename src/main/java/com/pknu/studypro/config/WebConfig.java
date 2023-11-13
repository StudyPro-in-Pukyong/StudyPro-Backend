package com.pknu.studypro.config;

import com.pknu.studypro.controller.auth.AuthArgumentResolver;
import com.pknu.studypro.controller.auth.KakaoTokenConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final KakaoTokenConverter kakaoTokenConverter;
    private final AuthArgumentResolver authArgumentResolver;

    public WebConfig(final KakaoTokenConverter kakaoTokenConverter,
                     final AuthArgumentResolver authArgumentResolver) {
        this.kakaoTokenConverter = kakaoTokenConverter;
        this.authArgumentResolver = authArgumentResolver;
    }

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(kakaoTokenConverter);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
