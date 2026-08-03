package com.techdevweb.techdevbackend.Notification.ServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdevweb.techdevbackend.Notification.Service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Render'in ucretsiz planinda giden SMTP portlari (25/465/587) engellendigi icin
 * mail gonderimi SMTP yerine Brevo'nun HTTP API'si (https://api.brevo.com/v3/smtp/email)
 * uzerinden yapiliyor. Bu, normal bir HTTPS istegi oldugundan Render'in SMTP port
 * kisitlamasindan etkilenmiyor.
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private static final String BREVO_ENDPOINT = "https://api.brevo.com/v3/smtp/email";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.mail.brevo.api-key:}")
    private String brevoApiKey;

    @Value("${app.mail.from:noreply@techdev.com}")
    private String fromAddress;

    @Value("${app.mail.from-name:SAU TechDev}")
    private String fromName;

    @Override
    public void send(String to, String subject, String body) {
        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            log.error("Email gönderilemedi: {} - Brevo API anahtarı (APP_MAIL_BREVO_API_KEY) tanımlanmamış", to);
            return;
        }
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sender", Map.of("name", fromName, "email", fromAddress));
            payload.put("to", List.of(Map.of("email", to)));
            payload.put("subject", subject);
            payload.put("htmlContent", "<pre style=\"font-family:inherit;white-space:pre-wrap;\">" + body + "</pre>");

            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_ENDPOINT))
                    .timeout(Duration.ofSeconds(15))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("api-key", brevoApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email gönderildi: {} - {}", to, subject);
            } else {
                log.error("Email gönderilemedi: {} - Brevo API {} döndü: {}", to, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            // Email gönderimi başarısız olsa bile ana işlemi (kayıt, onay vs.) etkilemesin
            log.error("Email gönderilemedi: {} - {}", to, e.getMessage());
        }
    }
}
