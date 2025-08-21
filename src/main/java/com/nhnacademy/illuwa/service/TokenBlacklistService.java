package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private static final String REFRESH_BLACKLIST_PREFIX = "blacklist:refresh:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;

    public void setBlacklistRefreshToken(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            return;
        }

        String rtHash = jwtProvider.hashRefreshToken(refreshToken);
        long ttl = jwtProvider.getRefreshTokenValidity();

        redisTemplate.opsForValue().set(
                REFRESH_BLACKLIST_PREFIX + rtHash,
                "blacklisted",
                ttl,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBlacklisted(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            return false;
        }
        String rtHash = jwtProvider.hashRefreshToken(refreshToken);
        return redisTemplate.hasKey(rtHash);
    }
}
