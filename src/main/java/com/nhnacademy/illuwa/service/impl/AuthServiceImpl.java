package com.nhnacademy.illuwa.service.impl;

import com.nhnacademy.illuwa.client.UserClient;
import com.nhnacademy.illuwa.common.jwt.JwtProvider;
import com.nhnacademy.illuwa.dto.LoginRequest;
import com.nhnacademy.illuwa.dto.MemberResponse;
import com.nhnacademy.illuwa.dto.RegisterRequest;
import com.nhnacademy.illuwa.dto.TokenResponse;
import com.nhnacademy.illuwa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient;
    private final JwtProvider jwtProvider;

    @Override
    public void signup(RegisterRequest memberRegisterRequest) {
        userClient.createMember(memberRegisterRequest);
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        MemberResponse memberResponse = userClient.login(loginRequest);

        Long userId = memberResponse.getMemberId();
        String role = memberResponse.getRole().toString();

        String token = jwtProvider.generateAccessToken(userId, role);
        return new TokenResponse(token);
    }
}
