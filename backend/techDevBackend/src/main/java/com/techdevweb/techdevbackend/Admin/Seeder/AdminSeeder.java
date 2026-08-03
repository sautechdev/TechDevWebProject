package com.techdevweb.techdevbackend.Admin.Seeder;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Enum.UserRole;
import com.techdevweb.techdevbackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Uygulama her baslarken calisir. Veritabaninda HIC admin yoksa (ozellikle
// projeyi GitHub'dan yeni ceken biri icin, veya "docker compose down -v" sonrasi
// sifirlanan bir ortamda), asagidaki bilgilerle otomatik bir admin hesabi olusturur.
// Boylece kimse "admin yapacak admin yok" acmazina dusmez.
//
// GUVENLIK: @Profile("!prod") ile bu seeder SADECE "prod" profili AKTIF DEGILKEN
// calisir. Yayinda (prod) bilinen bir varsayilan sifreyle admin acilmasin diye
// bilerek boyle ayarlandi - prod ortaminda admin'i elle/guvenli bir yontemle
// olusturun (ör. veritabanina dogrudan, guclu bir sifre ile).
@Profile("!prod")
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@techdev.com}")
    private String defaultAdminEmail;

    @Value("${app.admin.password:ChangeMe123!}")
    private String defaultAdminPassword;

    @Value("${app.admin.full-name:Varsayilan Admin}")
    private String defaultAdminFullName;

    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            log.info("En az bir admin zaten mevcut, AdminSeeder atlaniyor.");
            return;
        }

        // Ayni e-posta ile kayitli ama admin olmayan biri varsa, onu admin yap
        // (yeni kullanici olusturup e-posta cakismasi yaratmamak icin).
        User admin = userRepository.findByEmail(defaultAdminEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(defaultAdminEmail);
                    newUser.setPassword(passwordEncoder.encode(defaultAdminPassword));
                    newUser.setFullName(defaultAdminFullName);
                    return newUser;
                });

        admin.setRole(UserRole.ADMIN);
        admin.setEmailVerified(true);
        userRepository.save(admin);

        log.warn("Varsayilan admin hesabi olusturuldu -> email: {}, sifre: (app.admin.password ile ayarlandi). "
                + "Bu bilgileri production benzeri her ortamda mutlaka degistirin!", defaultAdminEmail);
    }
}
