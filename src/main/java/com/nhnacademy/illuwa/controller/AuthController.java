package com.nhnacademy.illuwa.controller;

import com.nhnacademy.illuwa.dto.*;
import com.nhnacademy.illuwa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/signup")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequest memberRegisterRequest) {
        authService.signup(memberRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/api/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody TokenRefreshRequest refreshRequest) {
        TokenResponse tokenResponse = authService.refreshAccessToken(refreshRequest.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/api/social-login")
    public ResponseEntity<TokenResponse> loginWithPayco(@RequestBody SocialLoginRequest request) {
        TokenResponse tokenResponse = authService.socialLogin(request);
        return ResponseEntity.ok(tokenResponse);
    }
}
