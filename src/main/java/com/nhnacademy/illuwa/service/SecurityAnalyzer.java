package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.dto.TokenContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityAnalyzer {

    public boolean isHighRisk(TokenContext original, String currentIp, String currentUserAgent) {
        List<String> risks = new ArrayList<>();

        // IP 주소 변경 체크
        if(!Objects.equals(original.getIpAddress(), currentIp)) {
            risks.add("IP 변경: " + original.getIpAddress() + " -> " +  currentIp);
        }

        // UserAgent 변경 체크
        if (isDifferentDeviceType(original.getUserAgent(), currentUserAgent)) {
            risks.add("디바이스 변경 탐지");
            return true; // 디바이스 변경은 즉시 위험 처리
        }

        // 최근 로그인 기준 5분 이내 IP 변경 체크
        Duration timeDiff = Duration.between(original.getLastUsedAt(), LocalDateTime.now());
        if (timeDiff.toMinutes() < 5 && !Objects.equals(original.getIpAddress(), currentIp)) {
            risks.add("단시간 내 IP 변경");
            return true;
        }

        if (!risks.isEmpty()) {
            log.warn("보안 위험 탐지: {}", String.join(", ", risks));
        }

        return risks.size() >= 2; // 2개 이상 위험 요소 시 차단
    }

    private boolean isDifferentDeviceType(String original, String current) {
        if (original == null || current == null) return false;

        boolean originalMobile = original.toLowerCase().contains("mobile");
        boolean currentMobile = current.toLowerCase().contains("mobile");
        return originalMobile != currentMobile;
    }
}
