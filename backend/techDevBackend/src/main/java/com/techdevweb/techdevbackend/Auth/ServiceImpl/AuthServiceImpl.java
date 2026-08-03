package com.techdevweb.techdevbackend.Auth.ServiceImpl;

import com.techdevweb.techdevbackend.Auth.DTO.AuthResponse;
import com.techdevweb.techdevbackend.Auth.DTO.LoginRequest;
import com.techdevweb.techdevbackend.Auth.DTO.RegisterRequest;
import com.techdevweb.techdevbackend.Auth.DTO.RegisterResponse;
import com.techdevweb.techdevbackend.Auth.DTO.ResendVerificationRequest;
import com.techdevweb.techdevbackend.Auth.DTO.VerifyEmailRequest;
import com.techdevweb.techdevbackend.Auth.Service.AuthService;
import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Enum.UserRole;
import com.techdevweb.techdevbackend.Exception.ConflictException;
import com.techdevweb.techdevbackend.Exception.EmailNotVerifiedException;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Notification.Service.MailService;
import com.techdevweb.techdevbackend.Repository.UserRepository;
import com.techdevweb.techdevbackend.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    @Value("${app.verification.code-expiry-minutes:15}")
    private long codeExpiryMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Bu e-posta adresi zaten kayitli");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        // GUVENLIK: role kasitli olarak RegisterRequest'ten degil, sabit USER olarak atanir.
        user.setRole(UserRole.USER);
        user.setEmailVerified(false);

        applyNewVerificationCode(user);
        userRepository.save(user);

        sendVerificationEmail(user);

        return new RegisterResponse(
                "Kayit basarili. E-posta adresinize gonderilen kodu girerek hesabinizi dogrulayin.",
                user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AccessDeniedException("E-posta veya sifre hatali"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AccessDeniedException("E-posta veya sifre hatali");
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "E-posta adresinizi henuz dogrulamadiniz. Lutfen mailinize gelen kodu girin.");
        }

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + request.getEmail()));

        if (user.isEmailVerified()) {
            throw new ConflictException("Bu hesap zaten dogrulanmis");
        }

        if (user.getVerificationCode() == null
                || user.getVerificationCodeExpiresAt() == null
                || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ConflictException("Dogrulama kodunun suresi dolmus. Yeni kod isteyin.");
        }

        if (!user.getVerificationCode().equals(request.getCode())) {
            throw new AccessDeniedException("Dogrulama kodu hatali");
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);

        // Basarili dogrulama sonrasi otomatik giris yaptiriyoruz - kullanici tekrar
        // login formuna girmek zorunda kalmasin.
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void resendVerificationCode(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + request.getEmail()));

        if (user.isEmailVerified()) {
            throw new ConflictException("Bu hesap zaten dogrulanmis, kod gondermeye gerek yok");
        }

        applyNewVerificationCode(user);
        userRepository.save(user);
        sendVerificationEmail(user);
    }

    // ------------------- Yardimci metotlar -------------------

    private void applyNewVerificationCode(User user) {
        String code = String.valueOf(100000 + RANDOM.nextInt(900000)); // 6 haneli, 100000-999999
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(codeExpiryMinutes));
    }

    private void sendVerificationEmail(User user) {
        String subject = "TechDev - E-posta Dogrulama Kodunuz";
        String body = "Merhaba " + user.getFullName() + ",\n\n"
                + "TechDev hesabinizi dogrulamak icin asagidaki kodu kullanin:\n\n"
                + user.getVerificationCode() + "\n\n"
                + "Bu kod " + codeExpiryMinutes + " dakika gecerlidir.\n\n"
                + "Bu istegi siz yapmadiysanız bu maili yok sayabilirsiniz.";

        mailService.send(user.getEmail(), subject, body);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
