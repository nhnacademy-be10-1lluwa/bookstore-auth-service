package com.nhnacademy.illuwa.service.impl;

import com.nhnacademy.illuwa.client.UserClient;
import com.nhnacademy.illuwa.common.jwt.JwtProvider;
import com.nhnacademy.illuwa.dto.MemberRegisterRequest;
import com.nhnacademy.illuwa.dto.SignupRequest;
import com.nhnacademy.illuwa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public void signup(SignupRequest signupRequest) {
        MemberRegisterRequest registerRequest = MemberRegisterRequest.builder()
                .name(signupRequest.getName())
                .birth(signupRequest.getBirth())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .contact(signupRequest.getContact())
                .build();

        userClient.createMember(registerRequest);
    }

//    @Override
//    public String login(LoginRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new InvalidEmailOrPasswordException("이메일 또는 비밀번호가 잘못되었습니다."));
//
//        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new InvalidEmailOrPasswordException("이메일 또는 비밀번호가 잘못되었습니다.");
//        }
//
//        return jwtProvider.generateAccessToken(user.getId());
//    }
}
