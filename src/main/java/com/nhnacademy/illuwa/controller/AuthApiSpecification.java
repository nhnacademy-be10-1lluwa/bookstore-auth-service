package com.nhnacademy.illuwa.controller;

import com.nhnacademy.illuwa.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증 API", description = "회원가입, 로그인, jwt 토큰 발급 등의 인증 관련 API")
public interface AuthApiSpecification {

    @Operation(summary = "회원가입", description = "사용자가 회원가입을 요청합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/signup")
    ResponseEntity<Void> signup(@RequestBody RegisterRequest memberRegisterRequest);

    @Operation(summary = "로그인", description = "이메일/비밀번호 기반 로그인 시 access/refresh 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "잘못된 자격 증명")
    })
    @PostMapping("/login")
    ResponseEntity<MemberLoginResponse> login(@RequestBody LoginRequest loginRequest,
                                                     HttpServletRequest request);


    @Operation(summary = "AccessToken 재발급", description = "RefreshToken을 통해 AccessToken을 재발급합니다.")
    @PostMapping("/refresh")
    ResponseEntity<TokenResponse> refreshToken(@RequestBody TokenRefreshRequest request,
                                                      HttpServletRequest httpRequest);

    @Operation(summary = "소셜 로그인(Payco)", description = "Payco를 통해 소셜 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/social-login")
    ResponseEntity<TokenResponse> loginWithPayco(@RequestBody SocialLoginRequest request);

    @Operation(summary = "로그아웃", description = "Access/Refresh 토큰을 무효화합니다.")
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestBody TokenResponse tokenResponse);
}
