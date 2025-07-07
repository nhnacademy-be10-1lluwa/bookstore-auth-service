package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.exception.InvalidTokenException;
import com.nhnacademy.illuwa.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final String KEY_PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;

    public void save(Long userId, String refreshToken) {
        long ttlMillis = jwtProvider.getRefreshTokenValidity();

        redisTemplate.opsForValue().set(
                key(refreshToken),
                String.valueOf(userId),
                ttlMillis,
                TimeUnit.MILLISECONDS);
    }

    public Long validate(String refreshToken) {

        jwtProvider.validateRefreshToken(refreshToken);

        String userId = redisTemplate.opsForValue().get(key(refreshToken));
        if(userId == null) {
            throw new InvalidTokenException("저장소에 존재하지 않는 Refresh Token");
        }

        return Long.valueOf(userId);
    }

    public void delete(String refreshToken) {
        redisTemplate.delete(key(refreshToken));
    }

    private String key(String refreshToken) {
        return KEY_PREFIX + refreshToken;
    }
}
