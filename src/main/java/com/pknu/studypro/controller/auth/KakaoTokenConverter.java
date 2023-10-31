package com.pknu.studypro.controller.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.service.auth.KakaoOAuthClient;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class KakaoTokenConverter implements Converter<String, KakaoUser> {

    private final KakaoOAuthClient oAuthClient;

    public KakaoTokenConverter(final KakaoOAuthClient oAuthClient) {
        this.oAuthClient = oAuthClient;
    }

    @Override
    public KakaoUser convert(final String token) {
        return oAuthClient.findByKakaoToken(token);
    }
}
