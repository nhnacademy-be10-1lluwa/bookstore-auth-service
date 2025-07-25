package com.nhnacademy.illuwa.client;

import com.nhnacademy.illuwa.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserClient {
    @PostMapping("/api/members")
    void createMember(@RequestBody RegisterRequest request);

    @PostMapping("/api/members/login")
    MemberResponse login(@RequestBody LoginRequest request);

    @PostMapping("/api/members/internal/social-members/check")
    ResponseEntity<MemberResponse> checkPaycoUser(@RequestBody PaycoMemberRequest request);

    @PostMapping("/api/members/internal/social-members")
    MemberResponse registerPaycoUser(@RequestBody PaycoMemberRequest request);
}