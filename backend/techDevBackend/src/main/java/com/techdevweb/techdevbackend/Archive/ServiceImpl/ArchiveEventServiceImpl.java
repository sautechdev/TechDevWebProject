package com.techdevweb.techdevbackend.Archive.ServiceImpl;

import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventRequest;
import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventResponse;
import com.techdevweb.techdevbackend.Archive.Entity.ArchiveEvent;
import com.techdevweb.techdevbackend.Archive.Mapper.ArchiveEventMapper;
import com.techdevweb.techdevbackend.Archive.Repository.ArchiveEventRepository;
import com.techdevweb.techdevbackend.Archive.Service.ArchiveEventService;
import com.techdevweb.techdevbackend.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "archiveEvents")
public class ArchiveEventServiceImpl implements ArchiveEventService {

    private final ArchiveEventRepository repository;
    private final ArchiveEventMapper mapper;

    @Override
    @CacheEvict(allEntries = true)
    public ArchiveEventResponse create(ArchiveEventRequest request) {
        ArchiveEvent entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public PageResponse<ArchiveEventResponse> getAll(String keyword, Integer year, Pageable pageable) {
        String searchKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        Page<ArchiveEvent> eventPage = repository.search(searchKeyword, year, pageable);

        List<ArchiveEventResponse> content = eventPage.getContent().stream()
                .map(mapper::toResponseWithoutItems) // liste görünümünde dosyaları yüklemiyoruz
                .collect(Collectors.toCollection(ArrayList::new));

        return PageResponse.<ArchiveEventResponse>builder()
                .content(content)
                .pageNumber(eventPage.getNumber())
                .pageSize(eventPage.getSize())
                .totalElements(eventPage.getTotalElements())
                .totalPages(eventPage.getTotalPages())
                .last(eventPage.isLast())
                .build();
    }

    @Override
    @Cacheable(key = "#id")
    public ArchiveEventResponse getById(Long id) {
        ArchiveEvent entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arşiv etkinliği bulunamadı: " + id));
        return mapper.toResponse(entity); // detayda dosyaları da getiriyoruz
    }

    @Override
    @CacheEvict(allEntries = true)
    public ArchiveEventResponse update(Long id, ArchiveEventRequest request) {
        ArchiveEvent entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arşiv etkinliği bulunamadı: " + id));
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setEventDate(request.getEventDate());
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
