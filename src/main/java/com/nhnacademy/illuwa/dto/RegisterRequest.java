package com.nhnacademy.illuwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String name;
    private LocalDate birth;
    private String email;
    private String password;
    private String contact;
    private String paycoId;
}

