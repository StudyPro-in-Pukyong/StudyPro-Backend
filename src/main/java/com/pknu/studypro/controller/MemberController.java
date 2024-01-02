package com.pknu.studypro.controller;

import com.pknu.studypro.controller.auth.Auth;
import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.service.MemberService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/find/member")
    public ResponseEntity settleClazzByTeacher(@RequestParam("user") String user1) throws UnsupportedEncodingException {
        // #을 기준으로 문자열 분리, Validation 으로 구분 할 수 있지 않을까?
        // #이 없는 경우 예외처리를 해야될 것 같음
        String user = URLDecoder.decode(user1, "UTF-8");
        if (user.contains("#")) {
            String[] parts = user.split("#");
            if (parts.length < 2) {
                // #이 사용자 문자열에 포함되어 있지 않은 경우 처리
                return new ResponseEntity<>("유효하지 않은 사용자 형식", HttpStatus.BAD_REQUEST);
            }
            String memberName = parts[0];
            long memberId = Integer.parseInt(parts[1]);
            System.out.println(memberName);
            Member response = memberService.verifiedMember(memberId, memberName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            System.out.println("실패" + user);
            return new ResponseEntity<>("#이 포함되어 있지 않습니다", HttpStatus.BAD_REQUEST);
        }
    }

}
