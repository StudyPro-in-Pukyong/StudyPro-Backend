package com.pknu.studypro.service.auth;

import com.pknu.studypro.controller.auth.Auth;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.dto.auth.RoleRequest;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.repository.MemberRepository;
import com.pknu.studypro.util.FindMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FindMember findMember;

    public AuthService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, FindMember findMember) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.findMember = findMember;
    }

    @Transactional
    public Member findKakaoMember(final KakaoUser kakaoUser) {
        return memberRepository.findByUsername(kakaoUser.id())
                .orElseGet(() -> memberRepository.save(kakaoUser.toMember()));
    }

    @Transactional
    public Tokens login(final Member member) {
        final String accessToken = jwtTokenProvider.createAccessFrom(member.getUsername());
        final String refresh = jwtTokenProvider.createRefresh();
        return new Tokens(accessToken, refresh);
    }

    public Tokens refresh(final Tokens tokens) {
        final String access = jwtTokenProvider.refreshAccessToken(tokens.access(), tokens.refresh());
        final String refresh = jwtTokenProvider.createRefresh();

        return new Tokens(access, refresh);
    }

    @Transactional
    public void changeRole(final LoginUser loginUser, final String role) {
        final Member member = findMember.findMemberByToken(loginUser);
        member.changeRole(Role.valueOf(role));
    }
}
