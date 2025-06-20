package com.nhnacademy.illuwa.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/test")
public class TestController {
    @GetMapping
    public String test() {
        return "Success! JWT 검증 완료.";
    }
}
