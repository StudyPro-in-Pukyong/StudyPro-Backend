package com.pknu.studypro.service;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.MemberRepository;
import com.pknu.studypro.util.FindMember;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FindMember findMember;

    public List<Member> verifiedMembers(Long memberId, String nickName, Role role) {
        if(memberId != null) {
            List<Member> list = new ArrayList<>();
            list.add(memberRepository.findByIdAndRole(memberId, role).orElse((null)));
            return list;
        }

        return memberRepository.findByNicknameAndRole(nickName, role);
    }

    public Member verifiedMember(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        return optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public Member verifiedMember(LoginUser loginUser) {
        return findMember.findMemberByToken(loginUser);
    }

    public Member updateMember(LoginUser loginUser, String nickname) {
        Member member = findMember.findMemberByToken(loginUser);
        member.setNickname(nickname);

        return memberRepository.save(member);
    }
}
