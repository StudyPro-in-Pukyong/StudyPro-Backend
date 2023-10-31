package com.pknu.studypro.service.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class KakaoOAuthClient {

    private final RestTemplate restTemplate;
    private final String url;

    public KakaoOAuthClient(final RestTemplate restTemplate,
                            @Value("${spring.auth.url}") final String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public KakaoUser findByKakaoToken(final String kakaoToken) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoToken);
        final RequestEntity<Void> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, URI.create(url));

        return restTemplate.exchange(requestEntity, KakaoUser.class).getBody();
    }

}
