package com.nhnacademy.illuwa.domain.auth.service;

import com.nhnacademy.illuwa.domain.user.dto.LoginRequest;

public interface AuthService {
    String login(LoginRequest request);
}
