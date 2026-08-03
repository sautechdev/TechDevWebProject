package com.techdevweb.techdevbackend.Config;

import com.techdevweb.techdevbackend.Security.JwtAuthenticationEntryPoint;
import com.techdevweb.techdevbackend.Security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RateLimitFilter rateLimitFilter;

    // Virgulle ayrilmis origin listesi - application.properties'ten okunur.
    // Yayina gecerken kod degistirmeden, sadece bu property'ye (veya karsilik gelen
    // ortam degiskenine) prod domain'ini eklemeniz yeterli. Varsayilan: sadece yerel Vite.
    @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Frontend farkli origin'den istek atiyor, tarayici bunu CORS ile engelliyor.
    // Bu bean, hangi origin'lere/metodlara/header'lara izin verildigini tanimlar.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // Tarayici her "gercek" istekten once bir OPTIONS (preflight) istegi atar,
                        // bu istek Authorization header'i tasimaz - bu yuzden mutlaka en basta
                        // ayri ve kosulsuz permitAll olmali, yoksa TUM CORS istekleri 403 alir.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Kayit/giris herkese acik
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        // Tech field/stack listesi herkese acik
                        .requestMatchers(HttpMethod.GET, "/api/tech-fields/**").permitAll()
                        .requestMatchers(HttpMethod.GET , "/api/tech-stacks/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tech-contents/**").permitAll()
                        // Etkinlikler listesi herkese açık
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                        // Projeler listesi/detayi public - "Proje Kesif" akisi login gerektirmez
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()
                        // Yetkinlik katalogu herkese acik (kayit/profil formunda dropdown icin)
                        .requestMatchers(HttpMethod.GET, "/api/skills").permitAll()
                        // WebSocket/SockJS el sikismasi (STOMP): token URL query parametresinde
                        // geliyor, bizim JwtAuthenticationFilter'imiz sadece Authorization header'ina
                        // bakiyor - bu yuzden burada engellersek WebSocket hic kurulamaz. Gercek
                        // kimlik dogrulama WebSocketAuthInterceptor tarafinda (STOMP CONNECT
                        // frame'inde) yapiliyor, HTTP katmaninda serbest birakiyoruz.
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Geri kalan her sey login ister
                        .anyRequest().authenticated()
                )
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
