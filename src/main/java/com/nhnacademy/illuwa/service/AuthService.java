package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.client.UserClient;
import com.nhnacademy.illuwa.jwt.JwtProvider;
import com.nhnacademy.illuwa.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public void signup(RegisterRequest req) {
        userClient.createMember(req);
    }

    public TokenResponse login(LoginRequest req) {
        MemberResponse memberResponse = userClient.login(req);

        Long userId = memberResponse.getMemberId();
        String role = memberResponse.getRole().toString();

        String access = jwtProvider.generateAccessToken(userId, role);
        String refresh = jwtProvider.generateRefreshToken();

        refreshTokenService.save(userId, refresh);

        long ttl = jwtProvider.getAccessTokenValidity() / 1000;
        return new TokenResponse(access, refresh, ttl);
    }

    public UserSession parse(String token) {
        Long userId = jwtProvider.getUserIdFromToken(token);
        String role = jwtProvider.getRoleFromToken(token);

        return new UserSession(userId, role);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        Long userId = refreshTokenService.validate(refreshToken);

        // 새 Access
        String role = "ROLE_USER";
        String newAccess = jwtProvider.generateAccessToken(userId, role);
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        return new TokenResponse(newAccess, ttl);
    }
}
