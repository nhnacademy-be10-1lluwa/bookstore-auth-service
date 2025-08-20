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

    private String keyByHash(String rtHash) {
        return KEY_PREFIX + rtHash;
    }

    public void save(Long userId, String rtHash) {
        long ttlMillis = jwtProvider.getRefreshTokenValidity();
        String key = keyByHash(rtHash);

        redisTemplate.opsForValue().set(
                key,
                String.valueOf(userId),
                ttlMillis,
                TimeUnit.MILLISECONDS);
    }

    public Long validate(String rtHash) {
        if (rtHash == null || rtHash.isBlank()) {
            throw new InvalidTokenException("INVALID_REFRESH_TOKEN", "리프레시 토큰이 없습니다.");
        }

        String key = keyByHash(rtHash);
        String userId = redisTemplate.opsForValue().get(key);
        if(userId == null) {
            throw new InvalidTokenException("REFRESH_TOKEN_NOT_FOUND", "리프레쉬 토큰이 존재하지 않습니다.");
        }
        return Long.valueOf(userId);
    }

    public void delete(String rtHash) {
        if(rtHash == null || rtHash.isBlank()) {
            return;
        }
        redisTemplate.delete(keyByHash(rtHash));
    }
}
