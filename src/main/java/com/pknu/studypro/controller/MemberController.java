package com.pknu.studypro.controller;

import com.pknu.studypro.controller.auth.Auth;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.MemberRequestDto;
import com.pknu.studypro.dto.MemberResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.mapper.MemberMapper;
import com.pknu.studypro.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Validated
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @PostMapping("/fcmToken")
    public ResponseEntity setFcmToken(@Auth final LoginUser loginUser,
                                      @RequestBody MemberRequestDto.Post fcmRequestDto) {
        System.out.println("!! token : " + fcmRequestDto.fcmToken());
        memberService.setFcmToken(loginUser, fcmRequestDto.fcmToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/mypage")
    public String mypage(@Auth final LoginUser loginUser,
//                         RedirectAttributes redirectAttributes) {
                         Model model) {
        Member member = memberService.verifiedMember(loginUser);
//        redirectAttributes.addFlashAttribute("id", member.getId());
//        redirectAttributes.addFlashAttribute("nickname", member.getNickname());
        model.addAttribute("id", member.getId());
        model.addAttribute("nickname", member.getNickname());

//        return "redirect:/mypage";
        return "mypage";
    }

    @GetMapping("/members")
    public ResponseEntity findMembers(@RequestParam("nickname") String nickname,
                                      @RequestParam("role") Role role) {
        List<Member> members = memberService.verifiedMembers(null, nickname, role);
        List<MemberResponseDto.Response> responses = memberMapper.membersToMemberResponseDtos(members);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/member")
    public ResponseEntity findMember(@Auth final LoginUser loginUser) {
        Member member = memberService.verifiedMember(loginUser);

        return new ResponseEntity<>(memberMapper.memberToMemberResponseDto(member), HttpStatus.OK);
    }

    @PutMapping("/member")
    public ResponseEntity updateMember(@Auth final LoginUser loginUser,
                                       @RequestBody MemberRequestDto.Put put) {
        Member member = memberService.updateMember(loginUser, put.getNickname());
        MemberResponseDto.Response response = memberMapper.memberToMemberResponseDto(member);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}