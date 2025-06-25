package com.nhnacademy.illuwa.domain.auth.service.impl;

import com.nhnacademy.illuwa.common.exception.InvalidEmailOrPasswordException;
import com.nhnacademy.illuwa.common.jwt.JwtProvider;
import com.nhnacademy.illuwa.domain.user.dto.LoginRequest;
import com.nhnacademy.illuwa.domain.user.entity.User;
import com.nhnacademy.illuwa.domain.user.repository.UserRepository;
import com.nhnacademy.illuwa.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidEmailOrPasswordException("이메일 또는 비밀번호가 잘못되었습니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidEmailOrPasswordException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        return jwtProvider.generateAccessToken(user.getId());
    }
}
