package com.nhnacademy.illuwa.client;

import com.nhnacademy.illuwa.dto.MemberRegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserClient {
    @PostMapping("/members")
    void createMember(@RequestBody MemberRegisterRequest request);
}