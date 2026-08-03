package com.techdevweb.techdevbackend.Tech.APIService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DevToService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final TranslationService translationService;

    // Bilinen doğru tag karşılıkları
    private static final Map<String, String> TAG_MAP = Map.ofEntries(
            Map.entry("C#", "csharp"),
            Map.entry("C++", "cpp"),
            Map.entry(".NET", "dotnet"),
            Map.entry("Node.js", "node"),
            Map.entry("Vue.js", "vue"),
            Map.entry("Next.js", "nextjs"),
            Map.entry("HTML/CSS", "css"),
            Map.entry("Spring Boot", "spring"),
            Map.entry("Tailwind CSS", "tailwindcss"),
            Map.entry("React Native", "reactnative"),
            Map.entry("Google Cloud Platform", "gcp"),
            Map.entry("Apache Kafka", "kafka"),
            Map.entry("Apache Spark", "spark"),

            // Niş konular için daha genel tag'lere yönlendir
            Map.entry("Scikit-learn", "machinelearning"),
            Map.entry("Assembly x86", "assembly"),
            Map.entry("Assembly ARM", "assembly"),
            Map.entry("Assembly MIPS", "assembly"),
            Map.entry("Dafny", "dafny"),
            Map.entry("TLA+", "tlaplus"),
            Map.entry("Coq", "coq"),
            Map.entry("Isabelle", "isabelle"),
            Map.entry("Lean", "lean")
    );
    public String fetchArticles(String techName) {
        String tag = TAG_MAP.getOrDefault(techName, normalizeTag(techName));

        List<DevToArticle> articles = fetchByTag(tag, 20);
        List<DevToArticle> filtered = applyFilter(articles, 50, 5, 3);

        if (filtered.isEmpty() && articles != null && !articles.isEmpty()) {
            filtered = applyFilter(articles, 10, 0, 0);
        }

        if (filtered.isEmpty()) {
            List<DevToArticle> fallback = fetchByTag(tag, 5);
            if (fallback != null && !fallback.isEmpty()) {
                filtered = fallback.stream().limit(3).toList();
            }
        }

        if (filtered.isEmpty()) {
            log.warn("Dev.to'da hiç içerik bulunamadı: {} (tag: {})", techName, tag);
            return null;
        }

        // Her makalenin title ve description'ını Türkçeye çevir
        for (DevToArticle article : filtered) {
            article.setTitle(translationService.translateToTurkish(article.getTitle()));
            article.setDescription(translationService.translateToTurkish(article.getDescription()));
        }

        try {
            log.info("Dev.to'dan {} makale çekildi ve çevrildi: {}", filtered.size(), techName);
            return objectMapper.writeValueAsString(filtered);
        } catch (Exception e) {
            log.warn("JSON dönüştürme hatası: {}", techName);
            return null;
        }
    }

    private List<DevToArticle> fetchByTag(String tag, int perPage) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("dev.to")
                            .path("/api/articles")
                            .queryParam("tag", tag)
                            .queryParam("per_page", perPage)
                            .build())
                    .retrieve()
                    .bodyToFlux(DevToArticle.class)
                    .timeout(Duration.ofSeconds(5))
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.warn("Dev.to isteği başarısız: {} - {}", tag, e.getMessage());
            return null;
        }
    }

    private List<DevToArticle> applyFilter(List<DevToArticle> articles, int minReactions,
                                           int minComments, int minReadTime) {
        if (articles == null) return List.of();
        return articles.stream()
                .filter(a -> a.getReactions() != null && a.getReactions() >= minReactions)
                .filter(a -> a.getComments() == null || a.getComments() >= minComments)
                .filter(a -> a.getReadingTime() == null || a.getReadingTime() >= minReadTime)
                .filter(a -> a.getTitle() != null)
                .sorted(Comparator.comparingInt(a -> -1 * (a.getReactions() != null ? a.getReactions() : 0)))
                .limit(5)
                .toList();
    }

    private String normalizeTag(String techName) {
        return techName.toLowerCase()
                .replace(" ", "")
                .replace("/", "")
                .replace(".", "")
                .replace("#", "sharp")
                .replace("+", "plus")
                .replace("(", "")
                .replace(")", "");
    }

    @Getter @Setter @NoArgsConstructor
    public static class DevToArticle {
        private Long id;
        private String title;
        private String description;
        private String url;

        @JsonProperty("readable_publish_date")
        private String publishedAt;

        @JsonProperty("positive_reactions_count")
        private Integer reactions;

        @JsonProperty("comments_count")
        private Integer comments;

        @JsonProperty("reading_time_minutes")
        private Integer readingTime;

        @JsonProperty("cover_image")
        private String coverImage;

        @JsonProperty("tag_list")
        private List<String> tags;
    }
}
