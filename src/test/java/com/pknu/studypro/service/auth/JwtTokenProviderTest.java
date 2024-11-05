package com.pknu.studypro.service.auth;

import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Value("${spring.auth.key}")
    private String key;
    @Value("${spring.auth.accessTokenExpired}")
    private Long accessTokenExpired;

    @Test
    @DisplayName("유저의 id로 액세스 토큰을 만들 수 있다")
    void createAccess() {
        //when
        final String token = jwtTokenProvider.createAccessFrom("test");

        //then
        assertThat(token.chars())
                .filteredOn(c -> c == '.')
                .hasSize(2);
    }

    @Test
    @DisplayName("리프레시 토큰을 만들 수 있다")
    void createRefresh() {
        //when
        final String token = jwtTokenProvider.createRefresh();

        //then
        assertThat(token.chars())
                .filteredOn(c -> c == '.')
                .hasSize(2);
    }

    @Test
    @DisplayName("토큰에서 id를 가져올 수 있다")
    void getMemberId() {
        //given
        final String token = jwtTokenProvider.createAccessFrom("참치");

        //when
        final String memberId = jwtTokenProvider.getUsername(token);

        //then
        assertThat(memberId).isEqualTo("참치");
    }

    @Test
    @DisplayName("액세스, 리프레시 토큰으로 리프레시를 할 수 있다")
    void refreshAccessFrom() {
        //given
        final String access = jwtTokenProvider.createAccessFrom("참치");
        final String refresh = jwtTokenProvider.createRefresh();

        //when
        final String refreshed = jwtTokenProvider.refreshAccessToken(access, refresh);

        //then
        assertThat(jwtTokenProvider.getUsername(refreshed)).isEqualTo("참치");
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로는 리프레시를 할 수 없다")
    void refreshAccessFrom_fail() {
        //given
        final JwtTokenProvider tokenProvider = new JwtTokenProvider(key, accessTokenExpired, 0);
        final String access = tokenProvider.createAccessFrom("참치");
        final String refresh = tokenProvider.createRefresh();

        //when, then
        assertThatThrownBy(() -> jwtTokenProvider.refreshAccessToken(access, refresh))
                .isInstanceOf(BusinessLogicException.class);
    }
}
