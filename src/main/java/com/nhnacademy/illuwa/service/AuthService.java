package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.client.UserClient;
import com.nhnacademy.illuwa.dto.enums.Status;
import com.nhnacademy.illuwa.jwt.JwtProvider;
import com.nhnacademy.illuwa.dto.*;
import feign.FeignException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public void signup(RegisterRequest req) {
        userClient.createMember(req);
    }

    public MemberLoginResponse login(LoginRequest req) {
        MemberResponse memberResponse = userClient.login(req);

        Long userId = memberResponse.getMemberId();
        String role = memberResponse.getRole().toString();
        Status status = memberResponse.getStatus();

        // Access Token
        String access = jwtProvider.generateAccessToken(userId, role);
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        // Refresh Token
        String refresh = jwtProvider.generateRefreshToken();
        String rtHash = jwtProvider.hashRefreshToken(refresh);
        refreshTokenService.save(userId, rtHash);

        return new MemberLoginResponse(access, refresh, ttl, status);
    }

    public TokenResponse refreshAccessToken(String refreshToken, String expiredAccessToken) {
        // 기존 RT 검증
        jwtProvider.validateRefreshToken(refreshToken);

        String rtHash = jwtProvider.hashRefreshToken(refreshToken);
        Long userId = refreshTokenService.validate(rtHash);

        // 만료된 AT 에서 role 추출
        Claims claims = jwtProvider.getClaimsAllowExpired(expiredAccessToken);
        String role = claims.get("role", String.class);

        // 새 AT 생성
        String newAccess = jwtProvider.generateAccessToken(userId, role);
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        // RT 로테이션 -> 기존 RT 폐기 + 새 RT 저장
        refreshTokenService.delete(rtHash);
        String newRefresh =  jwtProvider.generateRefreshToken();
        String newRtHash = jwtProvider.hashRefreshToken(newRefresh);
        refreshTokenService.save(userId, newRtHash);

        return new TokenResponse(newAccess, newRefresh, ttl);
    }

    public TokenResponse socialLogin(SocialLoginRequest request) {
        PaycoMemberRequest paycoMemberRequest = PaycoMemberRequest.of(request);
        MemberResponse member = findOrRegisterMember(paycoMemberRequest);

        Long userId = member.getMemberId();
        String role = member.getRole().toString();

        String access = jwtProvider.generateAccessToken(userId, role);
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        String refresh = jwtProvider.generateRefreshToken();
        String rtHash = jwtProvider.hashRefreshToken(refresh);
        refreshTokenService.save(userId, rtHash);

        return new TokenResponse(access, refresh, ttl);
    }

    private MemberResponse findOrRegisterMember(PaycoMemberRequest request) {
        try {
            ResponseEntity<MemberResponse> response = userClient.checkPaycoUser(request);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new IllegalStateException("회원 조회는 성공했으나 응답 본문이 없습니다.");
        } catch (FeignException.NotFound e) {
            // 회원이 없을 경우 회원 등록 시도
            MemberResponse registered = userClient.registerPaycoUser(request);
            if (registered == null) {
                throw new IllegalStateException("회원 등록 결과가 null입니다.");
            }
            return registered;
        }
    }

    public void logout(String accessToken, String refreshToken) {
        if(accessToken != null) {
            tokenBlacklistService.setBlacklistAccessToken(accessToken);
        }

        if(refreshToken != null) {
            refreshTokenService.delete(refreshToken);
        }
    }
}