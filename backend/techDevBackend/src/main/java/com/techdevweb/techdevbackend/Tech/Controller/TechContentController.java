package com.techdevweb.techdevbackend.Tech.Controller;

import com.techdevweb.techdevbackend.Tech.DTO.TechContentRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechContentResponse;
import com.techdevweb.techdevbackend.Tech.Service.TechContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tech-contents")
@RequiredArgsConstructor
public class TechContentController {

    private final TechContentService service;

    @GetMapping("/stack/{stackId}")
    public ResponseEntity<TechContentResponse> getByStackId(@PathVariable Long stackId) {
        return ResponseEntity.ok(service.getByStackId(stackId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/stack/{stackId}/custom")
    public ResponseEntity<TechContentResponse> updateCustomFields(
            @PathVariable Long stackId,
            @Valid @RequestBody TechContentRequest request) {
        return ResponseEntity.ok(service.updateCustomFields(stackId, request));
    }
}
