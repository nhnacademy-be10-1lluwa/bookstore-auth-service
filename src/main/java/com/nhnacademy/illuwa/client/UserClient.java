package com.nhnacademy.illuwa.client;

import com.nhnacademy.illuwa.dto.LoginRequest;
import com.nhnacademy.illuwa.dto.MemberResponse;
import com.nhnacademy.illuwa.dto.RegisterRequest;
import com.nhnacademy.illuwa.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserClient {
    @PostMapping("/members")
    void createMember(@RequestBody RegisterRequest request);

    @PostMapping("/members/login")
    MemberResponse login(@RequestBody LoginRequest request);
}