package com.pknu.studypro.service.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("첫 로그인이면 회원가입이 된다")
    void login() {
        //given
        final KakaoUser kakaoUser = KakaoUser.of("회원 아이디", "닉네임");

        //when
        authService.login(kakaoUser);

        //then
        assertThat(memberRepository.findByUsername("회원 아이디")).isPresent();
    }

    @Test
    @DisplayName("로그인을 하면 토큰을 발급한다")
    void login2() {
        //given
        final KakaoUser kakaoUser = KakaoUser.of("회원 아이디", "닉네임");

        //when
        final Tokens tokens = authService.login(kakaoUser);

        //then
        assertSoftly(softAssertions -> {
            assertThat(tokens.access()).contains(".", ".");
            assertThat(tokens.refresh()).contains(".", ".");
        });
    }
}
