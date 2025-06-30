package com.nhnacademy.illuwa.controller;

import com.nhnacademy.illuwa.dto.SignupRequest;
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
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @PostMapping
//    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
//        String token = authService.login(request);
//        return ResponseEntity.ok(new TokenResponse(token));
//    }
}
