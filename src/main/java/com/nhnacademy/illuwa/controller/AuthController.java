package com.nhnacademy.illuwa.controller;

import com.nhnacademy.illuwa.dto.*;
import com.nhnacademy.illuwa.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "회원가입, 로그인, jwt 토큰 발급 등의 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "사용자가 회원가입을 요청합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequest memberRegisterRequest) {
        authService.signup(memberRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호 기반 로그인 시 access/refresh 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "잘못된 자격 증명")
    })
    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody LoginRequest loginRequest) {
        MemberLoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }


    @Operation(summary = "AccessToken 재발급", description = "RefreshToken을 통해 AccessToken을 재발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken(), request.getAccessToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "소셜 로그인(Payco)", description = "Payco를 통해 소셜 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/social-login")
    public ResponseEntity<TokenResponse> loginWithPayco(@RequestBody SocialLoginRequest request) {
        TokenResponse tokenResponse = authService.socialLogin(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "로그아웃", description = "Access/Refresh 토큰을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody TokenResponse tokenResponse) {
        String accessToken = authHeader.substring(7); // "Bearer " 제거
        authService.logout(accessToken, tokenResponse.getRefreshToken());
        return ResponseEntity.ok().build();
    }
}
