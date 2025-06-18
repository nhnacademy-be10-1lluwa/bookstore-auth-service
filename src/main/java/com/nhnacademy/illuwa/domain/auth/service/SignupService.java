package com.nhnacademy.illuwa.domain.auth.service;

import com.nhnacademy.illuwa.domain.auth.dto.SignupRequest;

public interface SignupService {
    void registerUser(SignupRequest signupRequest);
}
