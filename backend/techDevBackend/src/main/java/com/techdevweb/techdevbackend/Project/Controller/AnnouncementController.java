package com.techdevweb.techdevbackend.Project.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.DTO.AnnouncementRequest;
import com.techdevweb.techdevbackend.Project.Entity.Announcement;
import com.techdevweb.techdevbackend.Project.Service.AnnouncementService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public List<Announcement> getAll(@PathVariable Long projectId) {
        return announcementService.getAnnouncements(projectId);
    }

    @PostMapping
    public Announcement create(@PathVariable Long projectId,
                                @RequestBody AnnouncementRequest request) {
        User currentUser = currentUserResolver.getCurrentUser();
        return announcementService.createAnnouncement(
                projectId, currentUser, request.getTitle(), request.getContent());
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                        @PathVariable Long announcementId) {
        User currentUser = currentUserResolver.getCurrentUser();
        announcementService.deleteAnnouncement(announcementId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
