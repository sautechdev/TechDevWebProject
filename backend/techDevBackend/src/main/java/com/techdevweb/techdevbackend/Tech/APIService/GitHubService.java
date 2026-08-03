package com.techdevweb.techdevbackend.Tech.APIService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${github.token:}")
    private String githubToken;

    // GitHub'ın resmi topic isimlerine göre eşleştirme
    private static final Map<String, String> TOPIC_MAP = Map.ofEntries(
            Map.entry("Java", "java"),
            Map.entry("C#", "csharp"),
            Map.entry("Python", "python"),
            Map.entry("Go", "golang"),
            Map.entry("Rust", "rust"),
            Map.entry("PHP", "php"),
            Map.entry("Ruby", "ruby"),
            Map.entry("Kotlin", "kotlin"),
            Map.entry("Node.js", "nodejs"),
            Map.entry("Spring Boot", "spring-boot"),
            Map.entry(".NET", "dotnet"),
            Map.entry("Django", "django"),
            Map.entry("FastAPI", "fastapi"),

            Map.entry("JavaScript", "javascript"),
            Map.entry("TypeScript", "typescript"),
            Map.entry("React", "reactjs"),
            Map.entry("Vue.js", "vuejs"),
            Map.entry("Angular", "angular"),
            Map.entry("Svelte", "svelte"),
            Map.entry("HTML/CSS", "css"),
            Map.entry("Next.js", "nextjs"),
            Map.entry("Tailwind CSS", "tailwindcss"),

            Map.entry("Swift", "swift"),
            Map.entry("Flutter", "flutter"),
            Map.entry("React Native", "react-native"),
            Map.entry("Dart", "dart"),
            Map.entry("Jetpack Compose", "jetpack-compose"),
            Map.entry("SwiftUI", "swiftui"),

            Map.entry("PostgreSQL", "postgresql"),
            Map.entry("MySQL", "mysql"),
            Map.entry("MongoDB", "mongodb"),
            Map.entry("Redis", "redis"),
            Map.entry("SQLite", "sqlite"),
            Map.entry("Oracle", "oracle"),
            Map.entry("Elasticsearch", "elasticsearch"),
            Map.entry("Cassandra", "cassandra"),
            Map.entry("Firebase", "firebase"),

            Map.entry("TensorFlow", "tensorflow"),
            Map.entry("PyTorch", "pytorch"),
            Map.entry("Scikit-learn", "scikit-learn"),
            Map.entry("Keras", "keras"),
            Map.entry("OpenCV", "opencv"),
            Map.entry("Hugging Face", "huggingface"),
            Map.entry("LangChain", "langchain"),
            Map.entry("Pandas", "pandas"),
            Map.entry("NumPy", "numpy"),

            Map.entry("Docker", "docker"),
            Map.entry("Kubernetes", "kubernetes"),
            Map.entry("Jenkins", "jenkins"),
            Map.entry("GitHub Actions", "github-actions"),
            Map.entry("Terraform", "terraform"),
            Map.entry("Ansible", "ansible"),
            Map.entry("Linux", "linux"),
            Map.entry("Nginx", "nginx"),

            Map.entry("AWS", "aws"),
            Map.entry("Azure", "azure"),
            Map.entry("Google Cloud Platform", "gcp"),
            Map.entry("DigitalOcean", "digitalocean"),
            Map.entry("Heroku", "heroku"),
            Map.entry("Cloudflare", "cloudflare"),
            Map.entry("Vercel", "vercel"),
            Map.entry("Netlify", "netlify"),

            Map.entry("Apache Spark", "apache-spark"),
            Map.entry("Hadoop", "hadoop"),
            Map.entry("Kafka", "kafka"),
            Map.entry("Hive", "hive"),
            Map.entry("Flink", "flink"),
            Map.entry("Airflow", "airflow"),
            Map.entry("Scala", "scala"),
            Map.entry("Databricks", "databricks"),

            Map.entry("Kali Linux", "kali-linux"),
            Map.entry("Metasploit", "metasploit"),
            Map.entry("Wireshark", "wireshark"),
            Map.entry("Burp Suite", "burp-suite"),
            Map.entry("OWASP", "owasp"),
            Map.entry("Nmap", "nmap"),
            Map.entry("Nessus", "nessus"),

            Map.entry("Solidity", "solidity"),
            Map.entry("Ethereum", "ethereum"),
            Map.entry("Hyperledger", "hyperledger"),
            Map.entry("Smart Contracts", "smart-contracts"),

            Map.entry("C", "c"),
            Map.entry("C++", "cpp"),
            Map.entry("Assembly x86", "assembly"),
            Map.entry("Assembly ARM", "arm"),
            Map.entry("Assembly MIPS", "mips"),
            Map.entry("Embedded C", "embedded-c"),
            Map.entry("RTOS", "rtos"),

            Map.entry("Dafny", "dafny"),
            Map.entry("TLA+", "tla-plus"),
            Map.entry("Coq", "coq"),
            Map.entry("Isabelle", "isabelle"),
            Map.entry("Lean", "lean")
    );

    public String fetchRepos(String techName) {
        String topic = TOPIC_MAP.getOrDefault(techName, normalizeTopic(techName));

        List<GitHubRepo> repos = searchByTopic(topic);
        sleepBetweenRequests();

        List<GitHubRepo> filtered = filterAndClean(repos, 500, 50);

        if (filtered.isEmpty()) {
            filtered = filterAndClean(repos, 50, 0);
        }

        if (filtered.isEmpty()) {
            repos = searchByKeyword(techName);
            sleepBetweenRequests();
            filtered = filterAndClean(repos, 100, 10);
        }

        if (filtered.isEmpty()) {
            log.warn("GitHub'da hiç uygun repo bulunamadı: {}", techName);
            return null;
        }

        try {
            log.info("GitHub'dan {} kaliteli repo çekildi: {}", filtered.size(), techName);
            return objectMapper.writeValueAsString(filtered);
        } catch (Exception e) {
            log.warn("JSON dönüştürme hatası: {}", techName);
            return null;
        }
    }
    private void sleepBetweenRequests() {
        try {
            Thread.sleep(2200); // 30 istek/dakika = ~2 saniyede 1 istek, güvenlik payı için 2.2s
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private List<GitHubRepo> searchByTopic(String topic) {
        return searchRepos("topic:" + topic);
    }

    private List<GitHubRepo> searchByKeyword(String techName) {
        String query = "\"" + techName + "\" in:name";
        return searchRepos(query);
    }

    private List<GitHubRepo> searchRepos(String query) {
        return searchReposWithRetry(query, 0);
    }

    private List<GitHubRepo> searchReposWithRetry(String query, int attempt) {
        try {
            WebClient.RequestHeadersSpec<?> request = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.github.com")
                            .path("/search/repositories")
                            .queryParam("q", query)
                            .queryParam("sort", "stars")
                            .queryParam("order", "desc")
                            .queryParam("per_page", 15)
                            .build())
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "TechDevWeb-App");

            if (githubToken != null && !githubToken.isBlank()) {
                request = request.header("Authorization", "Bearer " + githubToken);
            }

            GitHubResponse response = request
                    .retrieve()
                    .bodyToMono(GitHubResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            return response != null ? response.getItems() : null;

        } catch (WebClientResponseException e) {
            boolean isRateLimit = e.getStatusCode().value() == 403 || e.getStatusCode().value() == 429;

            if (isRateLimit && attempt < 2) {
                log.warn("Rate limit'e takıldı, {} saniye bekleniyor... (deneme {})", (attempt + 1) * 5, attempt + 1);
                try {
                    Thread.sleep((attempt + 1) * 5000L); // 5s, sonra 10s bekle
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                return searchReposWithRetry(query, attempt + 1);
            }

            log.warn("GitHub isteği başarısız: {} - {}", query, e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("GitHub isteği başarısız: {} - {}", query, e.getMessage());
            return null;
        }
    }

    private List<GitHubRepo> filterAndClean(List<GitHubRepo> repos, int minStars, int minForks) {
        if (repos == null) return List.of();

        return repos.stream()
                .filter(repo -> !repo.isArchived())
                .filter(repo -> repo.getStars() >= minStars)
                .filter(repo -> repo.getForks() >= minForks)
                .filter(repo -> repo.getDescription() != null)
                .filter(repo -> isEnglish(repo.getDescription()))
                .filter(repo -> isEnglish(repo.getFullName()))
                .limit(5)
                .toList();
    }

    private boolean isEnglish(String text) {
        if (text == null || text.isBlank()) return false;
        long nonLatinCount = text.chars()
                .filter(c -> !isLatinOrCommonSymbol(c))
                .count();
        double nonLatinRatio = (double) nonLatinCount / text.length();
        return nonLatinRatio < 0.15;
    }

    private boolean isLatinOrCommonSymbol(int c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || Character.isWhitespace(c)
                || ".,!?:;'\"-_()[]{}#@&+/\\%*=<>~`^$".indexOf(c) != -1;
    }

    private String normalizeTopic(String techName) {
        return techName.toLowerCase()
                .replace(" ", "-")
                .replace("/", "")
                .replace(".", "")
                .replace("#", "sharp")
                .replace("+", "plus")
                .replace("(", "")
                .replace(")", "");
    }

    @Getter @Setter @NoArgsConstructor
    public static class GitHubResponse {
        @JsonProperty("total_count")
        private Integer totalCount;
        private List<GitHubRepo> items;
    }

    @Getter @Setter @NoArgsConstructor
    public static class GitHubRepo {
        private Long id;

        @JsonProperty("full_name")
        private String fullName;

        private String description;

        @JsonProperty("html_url")
        private String url;

        @JsonProperty("stargazers_count")
        private Integer stars;

        @JsonProperty("forks_count")
        private Integer forks;

        private String language;

        private boolean archived;

        @JsonProperty("topics")
        private List<String> topics;

        @JsonProperty("updated_at")
        private String updatedAt;
    }
}
