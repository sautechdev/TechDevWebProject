package com.techdevweb.techdevbackend.Auth.Service;

import com.techdevweb.techdevbackend.Auth.DTO.AuthResponse;
import com.techdevweb.techdevbackend.Auth.DTO.LoginRequest;
import com.techdevweb.techdevbackend.Auth.DTO.RegisterRequest;
import com.techdevweb.techdevbackend.Auth.DTO.RegisterResponse;
import com.techdevweb.techdevbackend.Auth.DTO.ResendVerificationRequest;
import com.techdevweb.techdevbackend.Auth.DTO.VerifyEmailRequest;

public interface AuthService {

    // Artik token DONMEZ - hesap dogrulanmamis halde olusturulur, mail atilir
    RegisterResponse register(RegisterRequest request);

    // Dogrulanmamis hesapla giris denenirse EmailNotVerifiedException firlatir
    AuthResponse login(LoginRequest request);

    // Kod dogruysa hesabi aktiflestirir VE otomatik giris yapar (token doner)
    AuthResponse verifyEmail(VerifyEmailRequest request);

    // Kod gelmedi/suresi doldu ise yeni kod gonderir
    void resendVerificationCode(ResendVerificationRequest request);
}
