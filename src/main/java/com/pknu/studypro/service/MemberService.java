package com.pknu.studypro.service;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member verifiedMember(long memberId, String userName) {


        Optional<Member> member = memberRepository.findById(memberId);
        if (member.get().getUsername().equals(userName)) {
            return null;
        }
        return member.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
