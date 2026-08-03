package com.techdevweb.techdevbackend.Tech.APIService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TranslationService {

    private final WebClient.Builder webClientBuilder;

    @Value("${deepl.api-key:}")
    private String apiKey;

    // Free plan key'leri ":fx" ile biter, bu durumda "api-free" sunucusu kullanılmalı
    private static final String DEEPL_FREE_URL = "https://api-free.deepl.com/v2/translate";
    private static final String DEEPL_PRO_URL = "https://api.deepl.com/v2/translate";

    public TranslationService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String translateToTurkish(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("DeepL API key tanımlı değil, çeviri atlanıyor.");
            return text;
        }

        try {
            String url = apiKey.endsWith(":fx") ? DEEPL_FREE_URL : DEEPL_PRO_URL;

            Map<String, Object> requestBody = Map.of(
                    "text", List.of(text),
                    "target_lang", "TR",
                    "source_lang", "EN"
            );

            Map<String, Object> response = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "DeepL-Auth-Key " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response != null && response.get("translations") != null) {
                List<Map<String, Object>> translations = (List<Map<String, Object>>) response.get("translations");
                if (!translations.isEmpty()) {
                    return (String) translations.get(0).get("text");
                }
            }
        } catch (Exception e) {
            log.warn("Çeviri başarısız, orijinal metin kullanılacak: {}", e.getMessage());
        }

        return text;
    }
}