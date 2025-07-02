package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.dto.LoginRequest;
import com.nhnacademy.illuwa.dto.RegisterRequest;
import com.nhnacademy.illuwa.dto.TokenResponse;
import com.nhnacademy.illuwa.dto.UserSession;

public interface AuthService {
    void signup(RegisterRequest memberRegisterRequest);
    TokenResponse login(LoginRequest loginRequest);
    UserSession parse(String token);
}
