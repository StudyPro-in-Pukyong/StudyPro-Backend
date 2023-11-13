package com.pknu.studypro.controller.auth;

import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.dto.auth.RoleRequest;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/auth")
@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@RequestParam("token") final KakaoUser kakaoUser) {
        final Tokens tokens = authService.login(kakaoUser);
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
