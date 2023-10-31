package com.pknu.studypro.service.auth;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(final MemberRepository memberRepository, final JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public Tokens login(final KakaoUser kakaoUser) {
        final Member member = memberRepository.findByUsername(kakaoUser.id())
                .orElseGet(() -> memberRepository.save(kakaoUser.toMember()));

        final String accessToken = jwtTokenProvider.createAccessFrom(member.getUsername());
        final String refresh = jwtTokenProvider.createRefresh();
        return new Tokens(accessToken, refresh);
    }
}
