package com.pknu.studypro.controller;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.MemberResponseDto;
import com.pknu.studypro.mapper.MemberMapper;
import com.pknu.studypro.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @GetMapping("/members")
    public List<MemberResponseDto.Response> findMembers(@RequestParam("nickname") String nickname,
                                               @RequestParam("role") Role role) {
        List<Member> members = memberService.verifiedMember(null, nickname, role);

        return memberMapper.membersToMemberResponseDtos(members);
    }
}