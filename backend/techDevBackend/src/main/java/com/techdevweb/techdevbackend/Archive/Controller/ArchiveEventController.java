package com.techdevweb.techdevbackend.Archive.Controller;

import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventRequest;
import com.techdevweb.techdevbackend.Archive.DTO.ArchiveEventResponse;
import com.techdevweb.techdevbackend.Archive.Service.ArchiveEventService;
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
@RequestMapping("/api/archive-events")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ArchiveEventController {

    private final ArchiveEventService service;

    @GetMapping
    public ResponseEntity<PageResponse<ArchiveEventResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer year,
            @PageableDefault(size = 12, sort = "eventDate") Pageable pageable) {

        return ResponseEntity.ok(service.getAll(keyword, year, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArchiveEventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ArchiveEventResponse> create(@Valid @RequestBody ArchiveEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ArchiveEventResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody ArchiveEventRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
