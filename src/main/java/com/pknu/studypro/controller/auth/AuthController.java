package com.pknu.studypro.controller.auth;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.service.ClazzService;
import com.pknu.studypro.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/auth")
@Controller
@RequiredArgsConstructor
public class AuthController {
    @Value("${kakao.login_url}")
    private String loginUrlTemplate;

    @Value(("${kakao.client_id}"))
    private String clientId;

    @Value("${domain}")
    private String domain;

    @Value("${kakao.redirect_url}")
    private String redirectUri;

    private final AuthService authService;
    private final ClazzService clazzService;
    private final KakaoTokenConverter kakaoTokenConverter;

    // 기본 로그인 페이지
    @GetMapping("/login")
    public String index(Model model) {
        String loginUrl = String.format(loginUrlTemplate, clientId, domain+redirectUri);
        model.addAttribute("url", loginUrl);
        return "login"; // "login.html"을 반환 (템플릿 경로에서 "index" 템플릿을 찾음)
    }

    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam String code, RedirectAttributes redirectAttributes) {
        // 1. KakaoTokenConverter를 사용하여 KakaoUser 객체 생성
        KakaoUser kakaoUser = kakaoTokenConverter.convert(code);

        // 2. login 메서드 호출
        Member member = authService.findKakaoMember(kakaoUser);
        Tokens tokens = authService.login(member);

        // 3. 모델에 사용자 정보 추가
        redirectAttributes.addFlashAttribute("access", tokens.access());
        redirectAttributes.addFlashAttribute("refresh", tokens.refresh());
        redirectAttributes.addFlashAttribute("role", member.getRole());

        // 4. 모델를 반환
        if(member.getRole().equals(Role.ANONYMOUS)) { // 역할이 정해지지 않은 경우
            return "redirect:/auth/setMemberRole";
        } else {
            List<Clazz> clazzes = clazzService.getClazzes(member);
            if(clazzes.isEmpty()) return "redirect:/auth/initialClazz"; // 아직 클래스를 생성하지 않은 경우
            else return "redirect:/clazz"; // 클래스 화면으로 이동
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Tokens> refresh(@RequestBody final Tokens tokens) {
        final Tokens refreshed = authService.refresh(tokens);
        return ResponseEntity.ok(refreshed);
    }

    @PostMapping("/role")
    public String changeRole(@Auth final LoginUser loginUser, @RequestBody final String role, Model model) {
        model.addAttribute("role", role);
        authService.changeRole(loginUser, role);
            return "signinSuccess";
    }

    @GetMapping("/role")
    public ResponseEntity<LoginUser> getRole(@Auth final LoginUser loginUser) {
        return ResponseEntity.ok(loginUser);
    }

    // Create Clazz 화면 전환
    @GetMapping("/initialClazz")
    public String initialClazz(Model model) {
        return "initialClazz"; // 아직 클래스를 생성하지 않은 경우
    }

    // Member Role 설정
    @GetMapping("/setMemberRole")
    public String setMemberRole(Model model) {
        return "setMemberRole"; // 아직 클래스를 생성하지 않은 경우
    }
}