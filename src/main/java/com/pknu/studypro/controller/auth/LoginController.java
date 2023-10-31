package com.pknu.studypro.controller.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@RequestParam("token") final KakaoUser kakaoUser) {
        final Tokens tokens = authService.login(kakaoUser);
        return ResponseEntity.ok(tokens);
    }
}
