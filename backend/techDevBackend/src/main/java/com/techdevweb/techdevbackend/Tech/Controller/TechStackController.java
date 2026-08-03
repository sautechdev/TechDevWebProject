package com.techdevweb.techdevbackend.Tech.Controller;

import com.techdevweb.techdevbackend.Tech.DTO.TechStackRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechStackResponse;
import com.techdevweb.techdevbackend.Tech.Service.TechStackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tech-stacks")
@RequiredArgsConstructor
public class TechStackController {

    private final TechStackService service;

    @GetMapping("/search")
    public ResponseEntity<List<TechStackResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    @GetMapping("/field/{fieldId}")
    public ResponseEntity<List<TechStackResponse>> getByFieldId(@PathVariable Long fieldId) {
        return ResponseEntity.ok(service.getByFieldId(fieldId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechStackResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TechStackResponse> create(@Valid @RequestBody TechStackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TechStackResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody TechStackRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

