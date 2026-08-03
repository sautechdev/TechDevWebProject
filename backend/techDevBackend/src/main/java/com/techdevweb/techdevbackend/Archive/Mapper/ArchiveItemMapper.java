package com.techdevweb.techdevbackend.Archive.Mapper;

import com.techdevweb.techdevbackend.Archive.DTO.ArchiveItemResponse;
import com.techdevweb.techdevbackend.Archive.Entity.ArchiveItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ArchiveItemMapper {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public ArchiveItemResponse toResponse(ArchiveItem entity) {
        return ArchiveItemResponse.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .fileUrl(baseUrl + entity.getFilePath())
                .type(entity.getType())
                .fileSize(entity.getFileSize())
                .caption(entity.getCaption())
                .uploadedAt(entity.getUploadedAt())
                .archiveEventId(entity.getArchiveEvent().getId())
                .build();
    }
}
