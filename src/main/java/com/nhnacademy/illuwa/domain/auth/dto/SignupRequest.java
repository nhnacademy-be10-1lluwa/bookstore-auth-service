package com.nhnacademy.illuwa.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
