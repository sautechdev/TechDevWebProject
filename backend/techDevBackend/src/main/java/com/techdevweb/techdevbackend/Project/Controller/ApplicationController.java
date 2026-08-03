package com.techdevweb.techdevbackend.Project.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.DTO.ApplyRequest;
import com.techdevweb.techdevbackend.Project.Entity.Application;
import com.techdevweb.techdevbackend.Project.Service.ApplicationService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CurrentUserResolver currentUserResolver;

    // Basvuru formu submit edildiginde
    @PostMapping("/api/expertise-areas/{areaId}/applications")
    public Application apply(@PathVariable Long areaId,
                              @RequestBody ApplyRequest request) {
        User applicant = currentUserResolver.getCurrentUser();
        return applicationService.apply(areaId, applicant, request.getMessage());
    }

    // Profildeki "Basvurdugum projeler" sekmesi
    @GetMapping("/api/users/me/applications")
    public List<Application> myApplications() {
        User currentUser = currentUserResolver.getCurrentUser();
        return applicationService.getMyApplications(currentUser);
    }

    // Proje yoneticisinin gordugu basvuru listesi
    @GetMapping("/api/projects/{projectId}/applications")
    public List<Application> forProject(@PathVariable Long projectId) {
        User currentUser = currentUserResolver.getCurrentUser();
        return applicationService.getApplicationsForProject(projectId, currentUser);
    }

    @PutMapping("/api/applications/{applicationId}/approve")
    public Application approve(@PathVariable Long applicationId) {
        User currentUser = currentUserResolver.getCurrentUser();
        return applicationService.approve(applicationId, currentUser);
    }

    @PutMapping("/api/applications/{applicationId}/reject")
    public Application reject(@PathVariable Long applicationId) {
        User currentUser = currentUserResolver.getCurrentUser();
        return applicationService.reject(applicationId, currentUser);
    }
}
