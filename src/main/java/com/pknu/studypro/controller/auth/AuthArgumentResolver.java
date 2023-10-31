package com.pknu.studypro.controller.auth;

import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.repository.MemberRepository;
import com.pknu.studypro.service.auth.JwtTokenProvider;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.naming.AuthenticationException;

import static java.util.Objects.isNull;

@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public AuthArgumentResolver(final JwtTokenProvider jwtTokenProvider, final MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) throws Exception {
        final String token = getToken(webRequest);

        final String username = jwtTokenProvider.getUsername(token);
        final Role role = memberRepository.findRoleByUsername(username);

        validate(token, role);
        return new LoginUser(username, role);
    }

    private String getToken(final NativeWebRequest request) throws AuthenticationException {
        final String authorization = request.getHeader(AUTHORIZATION);
        if (isNull(authorization)) {
            throw new AuthenticationException();
        }

        return authorization.replace(BEARER, "");
    }

    private void validate(final String token, final Role role) {
        jwtTokenProvider.validate(token);
        if (isNull(role)) {
            throw new IllegalArgumentException();
        }
    }
}
