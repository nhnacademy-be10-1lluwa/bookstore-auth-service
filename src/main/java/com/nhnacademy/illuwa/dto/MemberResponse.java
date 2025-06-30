package com.nhnacademy.illuwa.dto;

import com.nhnacademy.illuwa.dto.enums.Role;
import com.nhnacademy.illuwa.dto.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private long memberId;
    private String name;
    private LocalDate birth;
    private String email;
    private Role role;
    private String contact;
    private String gradeName;
    private BigDecimal point;
    private Status status;
    private LocalDateTime lastLoginAt;
}
