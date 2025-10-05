package com.nhnacademy.illuwa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.dto.TokenContext;
import com.nhnacademy.illuwa.exception.InvalidTokenException;
import com.nhnacademy.illuwa.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private static final String KEY_PREFIX = "refresh:";
    private static final String CONTEXT_PREFIX = "context:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
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
        if(rtHash == null || rtHash.isBlank()) { return; }
        redisTemplate.delete(keyByHash(rtHash));
    }

    public void saveWithContext(Long userId, String rtHash, TokenContext tc) {
        long ttlMillis = jwtProvider.getRefreshTokenValidity();
        String key = keyByHash(rtHash);

        redisTemplate.opsForValue().set(
                key,
                String.valueOf(userId),
                ttlMillis,
                TimeUnit.MILLISECONDS);

        try {
            String contextJson = objectMapper.writeValueAsString(tc);
            redisTemplate.opsForValue().set(
                    CONTEXT_PREFIX + rtHash,
                    contextJson,
                    ttlMillis,
                    TimeUnit.MILLISECONDS
            );
        } catch (JsonProcessingException e) {
            log.warn("컨텍스트 저장 실패: {}", e.getMessage());
        }
    }

    public void deleteWithContext(String rtHash) {
        if(rtHash == null || rtHash.isBlank()) { return; }
        redisTemplate.delete(KEY_PREFIX + rtHash);
        redisTemplate.delete(CONTEXT_PREFIX + rtHash);
    }

    public TokenContext getContext(String rtHash) {
        try {
            String contextJson = redisTemplate.opsForValue().get(CONTEXT_PREFIX + rtHash);
            if (contextJson != null) {
                return objectMapper.readValue(contextJson, TokenContext.class);
            }
        } catch (Exception e) {
            log.warn("컨텍스트 조회 실패: {}", e.getMessage());
        }
        return null;
    }
}
