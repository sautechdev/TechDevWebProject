package com.techdevweb.techdevbackend.Event.Controller;

import com.techdevweb.techdevbackend.Event.DTO.EventRequest;
import com.techdevweb.techdevbackend.Event.DTO.EventResponse;
import com.techdevweb.techdevbackend.Event.Enum.EventStatus;
import com.techdevweb.techdevbackend.Event.Service.EventService;
import com.techdevweb.techdevbackend.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    // Herkese açık
    @GetMapping
    public ResponseEntity<PageResponse<EventResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EventStatus status,
            @PageableDefault(size = 10, sort = "startDateTime") Pageable pageable) {

        return ResponseEntity.ok(service.getAll(keyword, status, pageable));
    }

    // Herkese açık
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Sadece admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    // Sadece admin
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // Sadece admin
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Sadece admin
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }
}
