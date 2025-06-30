package com.nhnacademy.illuwa.service;

import com.nhnacademy.illuwa.dto.LoginRequest;
import com.nhnacademy.illuwa.dto.RegisterRequest;
import com.nhnacademy.illuwa.dto.TokenResponse;

public interface AuthService {
    void signup(RegisterRequest memberRegisterRequest);
    TokenResponse login(LoginRequest loginRequest);
}
