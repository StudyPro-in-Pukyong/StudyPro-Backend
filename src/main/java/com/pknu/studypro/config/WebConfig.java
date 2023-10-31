package com.pknu.studypro.config;

import com.pknu.studypro.controller.auth.KakaoTokenConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final KakaoTokenConverter kakaoTokenConverter;

    public WebConfig(final KakaoTokenConverter kakaoTokenConverter) {
        this.kakaoTokenConverter = kakaoTokenConverter;
    }

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(kakaoTokenConverter);
    }
}
