package com.nhnacademy.illuwa.controller;

import com.nhnacademy.illuwa.dto.LoginRequest;
import com.nhnacademy.illuwa.dto.RegisterRequest;
import com.nhnacademy.illuwa.dto.TokenResponse;
import com.nhnacademy.illuwa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequest memberRegisterRequest) {
        authService.signup(memberRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(tokenResponse);
    }
}
