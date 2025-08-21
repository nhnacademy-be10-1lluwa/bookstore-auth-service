package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.client.UserClient;
import com.nhnacademy.illuwa.dto.enums.Status;
import com.nhnacademy.illuwa.exception.InvalidTokenException;
import com.nhnacademy.illuwa.jwt.JwtProvider;
import com.nhnacademy.illuwa.dto.*;
import feign.FeignException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final SecurityAnalyzer securityAnalyzer;

    public void signup(RegisterRequest req) {
        userClient.createMember(req);
    }

//    public MemberLoginResponse login(LoginRequest req) {
//        MemberResponse memberResponse = userClient.login(req);
//
//        Long userId = memberResponse.getMemberId();
//        String role = memberResponse.getRole().toString();
//        Status status = memberResponse.getStatus();
//
//        // Access Token
//        String access = jwtProvider.generateAccessToken(userId, role);
//        long ttl = jwtProvider.getAccessTokenValidity() / 1000;
//
//        // Refresh Token
//        String refresh = jwtProvider.generateRefreshToken();
//        String rtHash = jwtProvider.hashRefreshToken(refresh);
//        refreshTokenService.save(userId, rtHash);
//
//        return new MemberLoginResponse(access, refresh, ttl, status);
//    }

//    public TokenResponse refreshAccessToken(String refreshToken, String expiredAccessToken) {
//        // 기존 RT 검증
//        jwtProvider.validateRefreshToken(refreshToken);
//
//        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
//            throw new InvalidTokenException("BLACKLISTED_REFRESH_TOKEN", "차단된 리프레시 토큰입니다.");
//        }
//
//        String rtHash = jwtProvider.hashRefreshToken(refreshToken);
//        Long userId = refreshTokenService.validate(rtHash);
//
//        // 만료된 AT 에서 role 추출
//        Claims claims = jwtProvider.getClaimsAllowExpired(expiredAccessToken);
//        String role = claims.get("role", String.class);
//
//        // 새 AT 생성
//        String newAccess = jwtProvider.generateAccessToken(userId, role);
//        long ttl = jwtProvider.getAccessTokenValidity() / 1000;
//
//        // RT 로테이션 -> 기존 RT 폐기 + 새 RT 저장
//        refreshTokenService.delete(rtHash);
//        String newRefresh =  jwtProvider.generateRefreshToken();
//        String newRtHash = jwtProvider.hashRefreshToken(newRefresh);
//        refreshTokenService.save(userId, newRtHash);
//
//        return new TokenResponse(newAccess, newRefresh, ttl);
//    }

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

    public void logout(String refreshToken) {
        if(refreshToken != null) {
            tokenBlacklistService.setBlacklistRefreshToken(refreshToken);
            String rtHash = jwtProvider.hashRefreshToken(refreshToken);
            refreshTokenService.delete(rtHash);
        }
    }

    public MemberLoginResponse loginWithContext(LoginRequest req, String clientIp, String userAgent) {
        MemberResponse memberResponse = userClient.login(req);

        Long userId = memberResponse.getMemberId();
        String role = memberResponse.getRole().toString();
        Status status = memberResponse.getStatus();

        // Access Token
        String access = jwtProvider.generateAccessToken(userId, role);
        long ttl = jwtProvider.getAccessTokenValidity() / 1000;

        // Refresh Token + 컨텍스트 저장
        String refresh = jwtProvider.generateRefreshToken();
        String rtHash = jwtProvider.hashRefreshToken(refresh);

        TokenContext tc = new TokenContext(rtHash, userId, clientIp, userAgent,
                LocalDateTime.now(), LocalDateTime.now()
        );
        refreshTokenService.saveWithContext(userId, rtHash, tc);

        return new MemberLoginResponse(access, refresh, ttl, status);
    }

    public TokenResponse refreshAccessTokenWithContext(String refreshToken, String accessToken, String clientIp, String userAgent) {
        // 1. RT 검증
        jwtProvider.validateRefreshToken(refreshToken);

        // 2. RT 블랙리스트 체크
        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new InvalidTokenException("BLACKLISTED_REFRESH_TOKEN", "차단된 리프레시 토큰입니다.");
        }

        String rtHash = jwtProvider.hashRefreshToken(refreshToken);
        Long userId = refreshTokenService.validate(rtHash);

        // 3. 컨텍스트 검증
        TokenContext context = refreshTokenService.getContext(rtHash);
        if (context != null) {
            boolean isHighRisk = securityAnalyzer.isHighRisk(context, clientIp, userAgent);
            if (isHighRisk) {
                // 고위험: RT 블랙리스트 추가 + 차단
                tokenBlacklistService.setBlacklistRefreshToken(refreshToken);
                log.warn("의심스러운 접근 차단 - UserId: {}, IP: {} → {}",
                        userId, context.getIpAddress(), clientIp);
                throw new SecurityException("의심스러운 접근이 탐지되었습니다.");
            }
        }

        // 4. RTR 진행
        Claims claims = jwtProvider.getClaimsAllowExpired(accessToken);
        String role = claims.get("role", String.class);

        // 새 AT, RT 생성
        String newAccess = jwtProvider.generateAccessToken(userId, role);
        String newRefresh =  jwtProvider.generateRefreshToken();
        String newRtHash = jwtProvider.hashRefreshToken(newRefresh);

        // 기존 RT + 컨텍스트 삭제 후 새로 저장
        refreshTokenService.deleteWithContext(newRtHash);
        TokenContext newContext = new TokenContext(newRtHash, userId, clientIp, userAgent,
                context.getCreatedAt(), LocalDateTime.now());
        refreshTokenService.saveWithContext(userId, newRtHash, newContext);

        long ttl = jwtProvider.getAccessTokenValidity() / 1000;
        return new TokenResponse(newAccess, newRefresh, ttl);
    }
}