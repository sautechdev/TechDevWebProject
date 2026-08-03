package com.techdevweb.techdevbackend.Event.Service;

import com.techdevweb.techdevbackend.Event.DTO.EventRequest;
import com.techdevweb.techdevbackend.Event.DTO.EventResponse;
import com.techdevweb.techdevbackend.Event.Enum.EventStatus;
import com.techdevweb.techdevbackend.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    EventResponse create(EventRequest request);
    PageResponse<EventResponse> getAll(String keyword, EventStatus statusFilter, Pageable pageable);
    EventResponse getById(Long id);
    EventResponse update(Long id, EventRequest request);
    void delete(Long id);
    EventResponse cancel(Long id);
}
