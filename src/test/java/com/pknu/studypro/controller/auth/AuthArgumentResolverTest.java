package com.pknu.studypro.controller.auth;

import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.repository.MemberRepository;
import com.pknu.studypro.service.auth.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

@SpringBootTest
class AuthArgumentResolverTest {

    @Autowired
    private AuthArgumentResolver resolver;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Auth 어노테이션이 있으면 리졸빙한다")
    void supportsParameter() {
        //given
        final MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(Auth.class))
                .willReturn(true);

        //when
        final boolean supports = resolver.supportsParameter(methodParameter);

        //then
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("토큰에서 username, role을 파싱한다")
    void resolveArgument() throws Exception {
        //given
        final NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        given(nativeWebRequest.getHeader("Authorization"))
                .willReturn("access.token.test");
        given(jwtTokenProvider.getUsername("access.token.test"))
                .willReturn("참치");
        given(memberRepository.findRoleByUsername("참치"))
                .willReturn(Role.STUDENT);

        //when
        final LoginUser loginUser = (LoginUser) resolver.resolveArgument(null, null, nativeWebRequest, null);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(loginUser.username()).isEqualTo("참치");
            softAssertions.assertThat(loginUser.role()).isEqualTo(Role.STUDENT);
        });
    }
}
