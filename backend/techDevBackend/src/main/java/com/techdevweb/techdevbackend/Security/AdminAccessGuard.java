package com.techdevweb.techdevbackend.Security;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Enum.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

// ProjectAccessGuard'in admin panelindeki karsiligi. Tum admin islemleri
// (kullanici yonetimi, proje moderasyonu vs.) bu sinif uzerinden kontrol edilir.
@Component
@RequiredArgsConstructor
public class AdminAccessGuard {

    private final CurrentUserResolver currentUserResolver;

    public User requireAdmin() {
        User user = currentUserResolver.getCurrentUser();
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Bu islem icin admin yetkisi gerekiyor");
        }
        return user;
    }
}
