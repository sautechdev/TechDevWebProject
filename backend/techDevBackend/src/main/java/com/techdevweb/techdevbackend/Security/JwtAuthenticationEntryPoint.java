package com.techdevweb.techdevbackend.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Token hic yoksa / gecersizse / suresi dolmussa buraya dusulur (kullanici "anonim"
// sayilir). Bunu AdminAccessGuard/ProjectAccessGuard'in firlattigi AccessDeniedException'dan
// (o zaten authenticated ama yetkisiz - 403 kalmali) AYIRMAK icin bilerek 401 donuyoruz.
// Frontend bu ayrimi soyle kullanmali: 401 -> token'i sil, login sayfasina yonlendir.
//                                       403 -> kullanici giris yapmis ama bu islem icin yetkisi yok, uyari goster.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "Giris yapmaniz gerekiyor veya oturumunuzun suresi dolmus olabilir");

        objectMapper.writeValue(response.getWriter(), body);
    }
}
