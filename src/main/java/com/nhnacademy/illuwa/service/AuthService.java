package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.client.UserClient;
import com.nhnacademy.illuwa.jwt.JwtProvider;
import com.nhnacademy.illuwa.dto.*;
import feign.FeignException;
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

    public void signup(RegisterRequest req) {
        userClient.createMember(req);
    }

    public TokenResponse login(LoginRequest req) {
        MemberResponse memberResponse = userClient.login(req);

        Long userId = memberResponse.getMemberId();
        String role = memberResponse.getRole().toString();

        String access = jwtProvider.generateAccessToken(userId, role);
        String refresh = jwtProvider.generateRefreshToken();
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        refreshTokenService.save(userId, refresh);

        return new TokenResponse(access, refresh, ttl);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        Long userId = refreshTokenService.validate(refreshToken);

        // 새 Access
        String role = "ROLE_USER";
        String newAccess = jwtProvider.generateAccessToken(userId, role);
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        return new TokenResponse(newAccess, ttl);
    }

    public TokenResponse socialLogin(SocialLoginRequest request) {
        PaycoMemberRequest paycoMemberRequest = PaycoMemberRequest.of(request);
        MemberResponse member = findOrRegisterMember(paycoMemberRequest);

        Long userId = member.getMemberId();
        String role = member.getRole().toString();
        log.info("Login Success: {}", member);

        String accessToken = jwtProvider.generateAccessToken(userId, role);
        String refreshToken = jwtProvider.generateRefreshToken();
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        refreshTokenService.save(userId, refreshToken);

        return new TokenResponse(accessToken, refreshToken, ttl);
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
}
