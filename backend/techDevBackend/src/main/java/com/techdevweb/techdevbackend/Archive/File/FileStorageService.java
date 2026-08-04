package com.techdevweb.techdevbackend.Archive.File;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Render'in ucretsiz planinda konteynerin diski KALICI DEGIL (her yeniden
 * baslatmada / deploy'da sifirlaniyor). Bu yuzden dosyalar yerel diske degil,
 * Cloudinary'ye (kalici, ucretsiz bulut depolama) yukleniyor.
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${app.cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${app.cloudinary.api-key:}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret:}")
    private String apiSecret;

    private Cloudinary cloudinary;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp",
            ".mp4", ".mov", ".avi", ".webm"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    @PostConstruct
    private void init() {
        if (cloudName == null || cloudName.isBlank()) {
            log.warn("Cloudinary yapilandirilmamis (APP_CLOUDINARY_CLOUD_NAME eksik). Dosya yuklemeleri basarisiz olacak.");
            return;
        }
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @SuppressWarnings("unchecked")
    public String store(MultipartFile file, Long eventId) {
        validateFile(file);
        if (cloudinary == null) {
            throw new RuntimeException("Cloudinary yapilandirilmamis. Lutfen ortam degiskenlerini kontrol edin.");
        }
        try {
            String publicId = "techdev/events/" + eventId + "/" + UUID.randomUUID();
            boolean isVideo = isVideoFile(file.getOriginalFilename());

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", isVideo ? "video" : "image",
                            "overwrite", true
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Dosya Cloudinary'e yuklendi: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("Dosya yuklenemedi: {}", e.getMessage());
            throw new RuntimeException("Dosya yuklenirken hata olustu: " + e.getMessage());
        }
    }

    public void delete(String fileUrl) {
        if (cloudinary == null || fileUrl == null || !fileUrl.startsWith("http")) return;
        try {
            String publicId = extractPublicId(fileUrl);
            if (publicId == null) {
                log.warn("Cloudinary public_id cikartilamadi: {}", fileUrl);
                return;
            }
            boolean isVideo = fileUrl.contains("/video/upload/");
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "resource_type", isVideo ? "video" : "image"
            ));
            log.info("Dosya Cloudinary'den silindi: {}", publicId);
        } catch (Exception e) {
            log.warn("Dosya silinemedi: {} - {}", fileUrl, e.getMessage());
        }
    }

    // https://res.cloudinary.com/{cloud}/image/upload/v169.../techdev/events/5/uuid.jpg -> techdev/events/5/uuid
    private String extractPublicId(String url) {
        Pattern pattern = Pattern.compile("/upload/(?:v\\d+/)?(.+)\\.[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }

    private boolean isVideoFile(String fileName) {
        String extension = getExtension(fileName).toLowerCase();
        return List.of(".mp4", ".mov", ".avi", ".webm").contains(extension);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Dosya boyutu 50MB'ı geçemez.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Geçersiz dosya adı.");
        }

        if (originalFileName.contains("..") || originalFileName.contains("/") || originalFileName.contains("\\")) {
            throw new IllegalArgumentException("Geçersiz dosya adı.");
        }

        String extension = getExtension(originalFileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("İzin verilmeyen dosya tipi: " + extension +
                    ". İzin verilen tipler: " + ALLOWED_EXTENSIONS);
        }
    }
}
