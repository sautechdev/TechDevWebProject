package com.techdevweb.techdevbackend.Archive.ServiceImpl;

import com.techdevweb.techdevbackend.Archive.Enum.ArchiveItemType;
import com.techdevweb.techdevbackend.Archive.DTO.ArchiveItemResponse;
import com.techdevweb.techdevbackend.Archive.Entity.ArchiveEvent;
import com.techdevweb.techdevbackend.Archive.Entity.ArchiveItem;
import com.techdevweb.techdevbackend.Archive.File.FileStorageService;
import com.techdevweb.techdevbackend.Archive.Mapper.ArchiveItemMapper;
import com.techdevweb.techdevbackend.Archive.Repository.ArchiveEventRepository;
import com.techdevweb.techdevbackend.Archive.Repository.ArchiveItemRepository;
import com.techdevweb.techdevbackend.Archive.Service.ArchiveItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ArchiveItemServiceImpl implements ArchiveItemService {

    private final ArchiveItemRepository itemRepository;
    private final ArchiveEventRepository eventRepository;
    private final ArchiveItemMapper mapper;
    private final FileStorageService fileStorageService;

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "archiveEvents", allEntries = true)
    })
    public ArchiveItemResponse upload(Long eventId, MultipartFile file, ArchiveItemType type, String caption) {
        ArchiveEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Arşiv etkinliği bulunamadı: " + eventId));

        String filePath = fileStorageService.store(file, eventId);

        ArchiveItem item = ArchiveItem.builder()
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .type(type)
                .fileSize(file.getSize())
                .caption(caption)
                .uploadedAt(LocalDateTime.now())
                .archiveEvent(event)
                .build();

        return mapper.toResponse(itemRepository.save(item));
    }

    @Override
    public List<ArchiveItemResponse> getByEventId(Long eventId) {
        return itemRepository.findByArchiveEventId(eventId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @CacheEvict(cacheNames = "archiveEvents", allEntries = true)
    public void delete(Long itemId) {
        ArchiveItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Dosya bulunamadı: " + itemId));

        fileStorageService.delete(item.getFilePath());
        itemRepository.delete(item);
    }
}
