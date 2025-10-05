package com.nhnacademy.illuwa.controller;

import com.nhnacademy.illuwa.dto.*;
import com.nhnacademy.illuwa.service.AuthService;
import com.nhnacademy.illuwa.service.IpContextService;
import com.nhnacademy.illuwa.service.SecurityAnalyzer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthApiSpecification {

    private final AuthService authService;
    private final IpContextService ipContextService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequest memberRegisterRequest) {
        authService.signup(memberRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody LoginRequest loginRequest,
                                                     HttpServletRequest request) {
        // IP, UserAgent 추출
        String clientIp = ipContextService.extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");

//        MemberLoginResponse loginResponse = authService.login(loginRequest);
        MemberLoginResponse loginResponse = authService.loginWithContext(
                loginRequest, clientIp, userAgent
        );

        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody TokenRefreshRequest request,
                                                      HttpServletRequest httpRequest) {
        // IP, UserAgent 추출
        String clientIp = ipContextService.extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

//        TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken(), request.getAccessToken());
        TokenResponse tokenResponse = authService.refreshAccessTokenWithContext(
                request.getRefreshToken(),
                request.getAccessToken(),
                clientIp,
                userAgent
        );

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/social-login")
    public ResponseEntity<TokenResponse> loginWithPayco(@RequestBody SocialLoginRequest request) {
        TokenResponse tokenResponse = authService.socialLogin(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenResponse tokenResponse) {
        authService.logout(tokenResponse.getRefreshToken());
//        authService.logout(tokenResponse.getAccessToken());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/fast")
    public ResponseEntity<String> fast() {
        return ResponseEntity.ok("OK");
    }
}
