package com.techdevweb.techdevbackend.Notification.Controller;

import com.techdevweb.techdevbackend.Notification.DTO.NotificationPreferenceRequest;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationPreferenceResponse;
import com.techdevweb.techdevbackend.Notification.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<NotificationPreferenceResponse> get() {
        return ResponseEntity.ok(service.getPreference());
    }

    @PutMapping
    public ResponseEntity<NotificationPreferenceResponse> update(@RequestBody NotificationPreferenceRequest request) {
        return ResponseEntity.ok(service.updatePreference(request));
    }
}
