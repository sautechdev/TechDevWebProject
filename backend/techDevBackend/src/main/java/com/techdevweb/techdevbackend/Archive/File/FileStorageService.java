package com.techdevweb.techdevbackend.Archive.File;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload-dir:/app/uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp",
            ".mp4", ".mov", ".avi", ".webm"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    public String store(MultipartFile file, Long eventId) {
        validateFile(file);
        try {
            // Klasör yolu: /app/uploads/events/{eventId}/
            String relativeDir = "/events/" + eventId;
            Path uploadPath = Paths.get(uploadDir + relativeDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Dosya adı çakışmasın diye UUID ekliyoruz
            String originalFileName = file.getOriginalFilename();
            String extension = getExtension(originalFileName);
            String uniqueFileName = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String relativeFilePath = "/uploads" + relativeDir + "/" + uniqueFileName;
            log.info("Dosya kaydedildi: {}", relativeFilePath);

            return relativeFilePath;

        } catch (IOException e) {
            log.error("Dosya kaydedilemedi: {}", e.getMessage());
            throw new RuntimeException("Dosya yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    public void delete(String relativeFilePath) {
        try {
            // relativeFilePath: /uploads/events/5/uuid.jpg -> gerçek path'e çevir
            String pathWithoutUploadsPrefix = relativeFilePath.replaceFirst("^/uploads", "");
            Path fullPath = Paths.get(uploadDir + pathWithoutUploadsPrefix);
            Files.deleteIfExists(fullPath);
            log.info("Dosya silindi: {}", relativeFilePath);
        } catch (IOException e) {
            log.warn("Dosya silinemedi: {} - {}", relativeFilePath, e.getMessage());
        }
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

        // Path traversal saldırısına karşı koruma
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
