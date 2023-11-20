package com.pknu.studypro.service.auth;

import com.pknu.studypro.domain.member.Member;
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
    public Tokens login(final KakaoUser kakaoUser) {
        final Member member = memberRepository.findByUsername(kakaoUser.id())
                .orElseGet(() -> memberRepository.save(kakaoUser.toMember()));

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
    public void changeRole(final LoginUser loginUser, final RoleRequest request) {
        final Member member = findMember.findMemberByToken(loginUser);
        member.changeRole(request.role());
    }

    // access 토큰을 재발행하기 위한 코드
    public Tokens accessToken(long userId) {
        final Member member = memberRepository.findById(userId).get();

        final String accessToken = jwtTokenProvider.createAccessFrom(member.getUsername());
        final String refresh = jwtTokenProvider.createRefresh();
        return new Tokens(accessToken, refresh);
    }
}
