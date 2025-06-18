package com.nhnacademy.illuwa.domain.auth.service.impl;

import com.nhnacademy.illuwa.common.exception.AlreadyExistsEmailException;
import com.nhnacademy.illuwa.domain.auth.dto.SignupRequest;
import com.nhnacademy.illuwa.domain.auth.entity.User;
import com.nhnacademy.illuwa.domain.auth.repository.UserRepository;
import com.nhnacademy.illuwa.domain.auth.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(SignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsEmailException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.createUser(request.getEmail(), encodedPassword);
        userRepository.save(user);
    }
}
