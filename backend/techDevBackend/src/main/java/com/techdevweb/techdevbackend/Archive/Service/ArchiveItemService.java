package com.techdevweb.techdevbackend.Archive.Service;

import com.techdevweb.techdevbackend.Archive.Enum.ArchiveItemType;
import com.techdevweb.techdevbackend.Archive.DTO.ArchiveItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArchiveItemService {
    ArchiveItemResponse upload(Long eventId, MultipartFile file, ArchiveItemType type, String caption);
    List<ArchiveItemResponse> getByEventId(Long eventId);
    void delete(Long itemId);
}
