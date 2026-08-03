package com.techdevweb.techdevbackend.Security;

import com.techdevweb.techdevbackend.Entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// GERCEK AUTH: JwtAuthenticationFilter, gecerli bir token gelince kullaniciyi
// SecurityContext'e koyuyor. Bu sinif sadece onu okuyup geri donduruyor.
// Eskiden (gecici cozum donemi) X-User-Id header'i okunuyordu, artik gerek yok.
@Component
public class CurrentUserResolver {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("Bu islem icin giris yapmaniz gerekiyor");
        }

        return user;
    }
}
