package com.nhnacademy.illuwa.controller;

import com.nhnacademy.illuwa.common.exception.DuplicateMemberException;
import com.nhnacademy.illuwa.dto.SignupRequest;
import com.nhnacademy.illuwa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequest signupRequest,
                         RedirectAttributes redirectAttributes) {
        try {
            authService.signup(signupRequest);
            return "redirect:/";
        } catch (DuplicateMemberException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }

//    @PostMapping
//    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
//        String token = authService.login(request);
//        return ResponseEntity.ok(new TokenResponse(token));
//    }
}
