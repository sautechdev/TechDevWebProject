package com.techdevweb.techdevbackend.Auth.Controller;

import com.techdevweb.techdevbackend.Auth.DTO.AuthResponse;
import com.techdevweb.techdevbackend.Auth.DTO.LoginRequest;
import com.techdevweb.techdevbackend.Auth.DTO.RegisterRequest;
import com.techdevweb.techdevbackend.Auth.DTO.RegisterResponse;
import com.techdevweb.techdevbackend.Auth.DTO.ResendVerificationRequest;
import com.techdevweb.techdevbackend.Auth.DTO.VerifyEmailRequest;
import com.techdevweb.techdevbackend.Auth.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/verify-email")
    public AuthResponse verifyEmail(@RequestBody VerifyEmailRequest request) {
        return authService.verifyEmail(request);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestBody ResendVerificationRequest request) {
        authService.resendVerificationCode(request);
        return ResponseEntity.noContent().build();
    }
}
