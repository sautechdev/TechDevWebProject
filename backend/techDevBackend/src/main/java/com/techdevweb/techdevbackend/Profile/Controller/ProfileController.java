package com.techdevweb.techdevbackend.Profile.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Profile.DTO.ProfileResponse;
import com.techdevweb.techdevbackend.Profile.DTO.UpdateProfileRequest;
import com.techdevweb.techdevbackend.Profile.Service.ProfileService;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Service.ProjectService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ProjectService projectService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public ProfileResponse getProfile() {
        User currentUser = currentUserResolver.getCurrentUser();
        return profileService.getProfile(currentUser);
    }

    @PutMapping
    public ProfileResponse updateProfile(@RequestBody UpdateProfileRequest request) {
        User currentUser = currentUserResolver.getCurrentUser();
        return profileService.updateProfile(currentUser, request);
    }

    // Profildeki "Olusturdugum projeler" sekmesi - PENDING/REJECTED dahil TUM durumlar gorunur
    @GetMapping("/projects")
    public List<Project> getMyProjects() {
        User currentUser = currentUserResolver.getCurrentUser();
        return projectService.getMyProjects(currentUser);
    }
}
