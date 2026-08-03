package com.techdevweb.techdevbackend.Tech.Controller;

import com.techdevweb.techdevbackend.Tech.DTO.TechFieldRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechFieldResponse;
import com.techdevweb.techdevbackend.Tech.Service.TechFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tech-fields")
@RequiredArgsConstructor
public class TechFieldController {

    private final TechFieldService service;

    @GetMapping
    public ResponseEntity<List<TechFieldResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechFieldResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TechFieldResponse> create(@Valid @RequestBody TechFieldRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TechFieldResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody TechFieldRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
