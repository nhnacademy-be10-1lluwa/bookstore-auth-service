package com.nhnacademy.illuwa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    private String provider;
    private String providerId;
    private Map<String, Object> attributes;
}
