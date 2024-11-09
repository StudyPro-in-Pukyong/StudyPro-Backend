package com.pknu.studypro.service.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
public class KakaoOAuthClient {
    private final RestTemplate restTemplate;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${domain}")
    private String domain;

    @Value("${kakao.redirect_url}")
    private String redirectUri;

    @Value("${kakao.token_url}")
    private String tokenUrl;

    @Value("${kakao.info_url}")
    private String userInfoUrl;

    public KakaoOAuthClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 토큰을 이용해서 KakaoUser 객체 생성
    public KakaoUser findByKakaoToken(final String kakaoToken) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoToken);
        final RequestEntity<Void> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, URI.create(userInfoUrl));

        Map<String, Object> response = restTemplate.exchange(requestEntity, Map.class).getBody();

        KakaoUser kakaoUser = restTemplate.exchange(requestEntity, KakaoUser.class).getBody();
        return kakaoUser;
    }

    // 카카오 로그인 시 발급된 code를 이용해서 토큰 발급하기
    public String findKakaoToken(final String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", domain+redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                kakaoTokenRequest,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        return (String) responseBody.get("access_token");
    }
}
