package com.techdevweb.techdevbackend.Archive.Mapper;

import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventRequest;
import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventResponse;
import com.techdevweb.techdevbackend.Archive.Entity.ArchiveEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ArchiveEventMapper {

    private final ArchiveItemMapper archiveItemMapper;

    public ArchiveEventMapper(ArchiveItemMapper archiveItemMapper) {
        this.archiveItemMapper = archiveItemMapper;
    }

    public ArchiveEvent toEntity(ArchiveEventRequest request) {
        return ArchiveEvent.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .build();
    }

    public ArchiveEventResponse toResponse(ArchiveEvent entity) {
        return ArchiveEventResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .eventDate(entity.getEventDate())
                .items(entity.getItems() == null
                        ? new ArrayList<>()
                        : entity.getItems().stream()
                        .map(archiveItemMapper::toResponse)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }

    public ArchiveEventResponse toResponseWithoutItems(ArchiveEvent entity) {
        return ArchiveEventResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .eventDate(entity.getEventDate())
                .items(new ArrayList<>())
                .build();
    }
}
