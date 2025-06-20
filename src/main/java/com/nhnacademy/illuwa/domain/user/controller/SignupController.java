package com.nhnacademy.illuwa.domain.user.controller;

import com.nhnacademy.illuwa.domain.user.dto.SignupRequest;
import com.nhnacademy.illuwa.domain.user.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/signup")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        signupService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
