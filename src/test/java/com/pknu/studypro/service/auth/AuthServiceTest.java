package com.pknu.studypro.service.auth;

import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.dto.auth.RoleRequest;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("첫 로그인이면 회원가입이 된다")
    void login() {
        //given
        final KakaoUser kakaoUser = KakaoUser.of("username", "nickname", "email", "phoneNumber", "profileImage");

        //when
        authService.findKakaoMember(kakaoUser);

        //then
        assertThat(memberRepository.findByUsername("username")).isPresent();
    }

    @Test
    @DisplayName("로그인을 하면 토큰을 발급한다")
    void login2() {
        //given
        final Member member = new Member(Role.ANONYMOUS, LoginType.KAKAO,
                "username", null, "nickname", "email", "phoneNumber");

        //when
        final Tokens tokens = authService.login(member);

        //then
        assertSoftly(softAssertions -> {
            assertThat(tokens.access()).contains(".", ".");
            assertThat(tokens.refresh()).contains(".", ".");
        });
    }

    @Test
    @DisplayName("리프레시할 수 있다")
    void refresh() {
        //given
        final Member member = new Member(Role.ANONYMOUS, LoginType.KAKAO,
                "username", null, "nickname", "email", "phoneNumber");
        final Tokens tokens = authService.login(member);

        //when
        final Tokens refresh = authService.refresh(tokens);

        //then
        final String memberId = jwtTokenProvider.getUsername(tokens.access());
        final String memberIdRefreshed = jwtTokenProvider.getUsername(refresh.access());
        assertThat(memberIdRefreshed).isEqualTo(memberId);
    }

    @Test
    @DisplayName("유저의 권한을 변경할 때 존재하지 않는 유저라면 예외가 발생한다")
    void changeRole() {
        //given
        final LoginUser notExistUser = new LoginUser("nothing", Role.ANONYMOUS);

        //when, then
        assertThatThrownBy(() -> authService.changeRole(notExistUser, "STUDENT"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
