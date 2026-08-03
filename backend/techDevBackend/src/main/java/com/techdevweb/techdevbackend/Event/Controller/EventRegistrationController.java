package com.techdevweb.techdevbackend.Event.Controller;

import com.techdevweb.techdevbackend.Event.DTO.EventRegistrationResponse;
import com.techdevweb.techdevbackend.Event.Service.EventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService service;

    @PostMapping
    public ResponseEntity<EventRegistrationResponse> register(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(eventId));
    }

    @DeleteMapping
    public ResponseEntity<Void> unregister(@PathVariable Long eventId) {
        service.unregister(eventId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<EventRegistrationResponse>> getByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getByEventId(eventId));
    }

    // Admin — onaylar
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{registrationId}/approve")
    public ResponseEntity<EventRegistrationResponse> approve(@PathVariable Long eventId,
                                                             @PathVariable Long registrationId) {
        return ResponseEntity.ok(service.approve(eventId, registrationId));
    }

    // Admin — reddeder
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{registrationId}/reject")
    public ResponseEntity<EventRegistrationResponse> reject(@PathVariable Long eventId,
                                                            @PathVariable Long registrationId) {
        return ResponseEntity.ok(service.reject(eventId, registrationId));
    }
}
