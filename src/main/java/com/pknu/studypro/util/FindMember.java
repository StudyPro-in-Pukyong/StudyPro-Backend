package com.pknu.studypro.util;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FindMember {
    private final MemberRepository memberRepository;

    // access 토큰값으로 member 반환
    public Member findMemberByToken(LoginUser loginUser) {
        return memberRepository.findByUsername(loginUser.username())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}
