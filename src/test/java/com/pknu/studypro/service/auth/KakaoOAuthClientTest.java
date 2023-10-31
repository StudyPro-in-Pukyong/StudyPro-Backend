package com.pknu.studypro.service.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class KakaoOAuthClientTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private KakaoOAuthClient kakaoOAuthClient;

    @Test
    @DisplayName("카카오 토큰으로 유저 정보 조회하기")
    void findByKakaoToken() {
        //given
        final KakaoUser expected = new KakaoUser("id", null);
        given(restTemplate.exchange(any(RequestEntity.class), any(Class.class)))
                .willReturn(ResponseEntity.of(Optional.of(expected)));

        //when
        final KakaoUser user = kakaoOAuthClient.findByKakaoToken("kakao-token");

        //then
        assertThat(user).isEqualTo(expected);
        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), any(Class.class));
    }

}
