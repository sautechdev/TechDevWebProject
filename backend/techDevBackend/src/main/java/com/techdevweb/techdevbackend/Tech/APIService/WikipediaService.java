package com.techdevweb.techdevbackend.Tech.APIService;

import com.techdevweb.techdevbackend.Tech.Mapper.WikipediaTitleMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WikipediaService {

    private final WebClient.Builder webClientBuilder;
    private final WikipediaTitleMapper titleMapper;
    private final TranslationService translationService;

    private static final String SUMMARY_API = "https://en.wikipedia.org/api/rest_v1/page/summary/";
    private static final String SEARCH_API = "https://en.wikipedia.org/w/api.php";

    public String fetchSummary(String techName) {
        String resolvedTitle = titleMapper.resolve(techName);

        String result = fetchFromWikipedia(resolvedTitle);
        if (result == null) {
            String searchedTitle = searchWikipedia(techName);
            if (searchedTitle != null) {
                result = fetchFromWikipedia(searchedTitle);
            }
        }

        if (result == null) {
            log.warn("Hiçbir yöntemle bulunamadı: {}", techName);
            return null;
        }

        //İngilizce içerik Türkçeye çevriliyor
        String translated = translationService.translateToTurkish(result);
        log.info("Wikipedia özeti Türkçeye çevrildi: {}", techName);
        return translated != null ? translated.trim() : null;
    }

    private String fetchFromWikipedia(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8)
                    .replace("+", "_");
            URI uri = URI.create(SUMMARY_API + encodedTitle);

            WikipediaResponse response = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(WikipediaResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response != null && response.getExtract() != null) {
                log.info("Wikipedia'dan çekildi: {}", title);
                return response.getExtract();
            }
        } catch (Exception e) {
            log.warn("Direkt eşleşme bulunamadı: {} - {}", title, e.getMessage());
        }
        return null;
    }

    private String searchWikipedia(String query) {
        try {
            SearchResponse response = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("en.wikipedia.org")
                            .path("/w/api.php")
                            .queryParam("action", "query")
                            .queryParam("list", "search")
                            .queryParam("srsearch", query)
                            .queryParam("format", "json")
                            .queryParam("srlimit", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(SearchResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response != null && response.getQuery() != null
                    && !response.getQuery().getSearch().isEmpty()) {
                String foundTitle = response.getQuery().getSearch().get(0).getTitle();
                log.info("Search API ile bulundu: {} -> {}", query, foundTitle);
                return foundTitle;
            }
        } catch (Exception e) {
            log.warn("Search API'de bulunamadı: {} - {}", query, e.getMessage());
        }
        return null;
    }

    @Getter @Setter @NoArgsConstructor
    public static class WikipediaResponse {
        private String title;
        private String extract;
        private String description;
    }

    @Getter @Setter @NoArgsConstructor
    public static class SearchResponse {
        private QueryResult query;
    }

    @Getter @Setter @NoArgsConstructor
    public static class QueryResult {
        private List<SearchItem> search;
    }

    @Getter @Setter @NoArgsConstructor
    public static class SearchItem {
        private String title;
    }
}
