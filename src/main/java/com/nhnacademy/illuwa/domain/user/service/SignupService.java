package com.nhnacademy.illuwa.domain.user.service;

import com.nhnacademy.illuwa.domain.user.dto.SignupRequest;

public interface SignupService {
    void registerUser(SignupRequest request);
}
