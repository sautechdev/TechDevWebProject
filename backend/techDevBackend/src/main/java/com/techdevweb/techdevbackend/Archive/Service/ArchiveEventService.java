package com.techdevweb.techdevbackend.Archive.Service;

import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventRequest;
import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventResponse;
import com.techdevweb.techdevbackend.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArchiveEventService {
    ArchiveEventResponse create(ArchiveEventRequest request);
    PageResponse<ArchiveEventResponse> getAll(String keyword, Integer year, Pageable pageable);
    ArchiveEventResponse getById(Long id);
    ArchiveEventResponse update(Long id, ArchiveEventRequest request);
    void delete(Long id);
}
