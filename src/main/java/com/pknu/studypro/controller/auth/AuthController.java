package com.pknu.studypro.controller.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.dto.auth.RoleRequest;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@Controller
@RequiredArgsConstructor
public class AuthController {
    @Value("${kakao.login_url}")
    private String loginUrlTemplate;

    @Value(("${kakao.client_id}"))
    private String clientId;

    @Value("${kakao.redirect_url}")
    private String redirectUri;

    private final AuthService authService;
    private final KakaoTokenConverter kakaoTokenConverter;

    // 기본 로그인 페이지
    @GetMapping("/login")
    public String index(Model model) {
        String loginUrl = String.format(loginUrlTemplate, clientId, redirectUri);
        model.addAttribute("url", loginUrl);
        return "login"; // "login.html"을 반환 (템플릿 경로에서 "index" 템플릿을 찾음)
    }

    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam String code, Model model) {
        // 1. KakaoTokenConverter를 사용하여 KakaoUser 객체 생성
        KakaoUser kakaoUser = kakaoTokenConverter.convert(code);

        // 2. login 메서드 호출
        Tokens tokens = authService.login(kakaoUser);

        // 3. 모델에 사용자 정보 추가
        model.addAttribute("access", tokens.access());
        model.addAttribute("refresh", tokens.refresh());

        return "signinSuccess";
    }

    // access 토큰을 재발행하기 위한 코드
    @PostMapping("/access/{userId}")
    public ResponseEntity<Tokens> test(@PathVariable("userId") long userId) {
        final Tokens tokens = authService.accessToken(userId);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Tokens> refresh(@RequestBody final Tokens tokens) {
        final Tokens refreshed = authService.refresh(tokens);
        return ResponseEntity.ok(refreshed);
    }

    @PostMapping("/role")
    public ResponseEntity<Void> changeRole(@Auth final LoginUser loginUser,
                                           @RequestBody final RoleRequest request) {
        authService.changeRole(loginUser, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role")
    public ResponseEntity<LoginUser> getRole(@Auth final LoginUser loginUser) {
        return ResponseEntity.ok(loginUser);
    }
}
