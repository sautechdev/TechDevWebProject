package com.techdevweb.techdevbackend.Archive.Controller;

import com.techdevweb.techdevbackend.Archive.Enum.ArchiveItemType;
import com.techdevweb.techdevbackend.Archive.DTO.ArchiveItemResponse;
import com.techdevweb.techdevbackend.Archive.Service.ArchiveItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/archive-items")
@RequiredArgsConstructor
public class ArchiveItemController {

    private final ArchiveItemService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ArchiveItemResponse> upload(
            @RequestParam("eventId") Long eventId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") ArchiveItemType type,
            @RequestParam(value = "caption", required = false) String caption) {

        ArchiveItemResponse response = service.upload(eventId, file, type, caption);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ArchiveItemResponse>> getByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getByEventId(eventId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable Long itemId) {
        service.delete(itemId);
        return ResponseEntity.noContent().build();
    }
}
