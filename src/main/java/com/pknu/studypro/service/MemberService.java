package com.pknu.studypro.service;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> verifiedMember(Long memberId, String nickName, Role role) {
        if(memberId != null) {
            List<Member> list = new ArrayList<>();
            list.add(memberRepository.findByIdAndRole(memberId, role).orElse((null)));
            return list;
        }

        return memberRepository.findByNicknameAndRole(nickName, role);
    }
}
