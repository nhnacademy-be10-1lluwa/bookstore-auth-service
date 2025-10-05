package com.nhnacademy.illuwa.dto;


import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenContext {
    private String refreshTokenHash;
    private Long userId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
}
