package com.techdevweb.techdevbackend.Admin.Controller;

import com.techdevweb.techdevbackend.Admin.DTO.AdminCreateProjectRequest;
import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Enum.ProjectStatus;
import com.techdevweb.techdevbackend.Project.Service.ProjectService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectService projectService;
    private final CurrentUserResolver currentUserResolver;

    // Tum durumlar dahil (PENDING/REJECTED de gorunur) - status filtresi opsiyonel
    @GetMapping
    public Page<Project> getAllProjects(
            @RequestParam(required = false) ProjectStatus status,
            Pageable pageable) {
        User admin = currentUserResolver.getCurrentUser();
        return projectService.getAllProjectsAdmin(status, pageable, admin);
    }

    // Onay bekleyen projeler - moderasyon kuyrugu
    @GetMapping("/pending")
    public List<Project> getPendingProjects() {
        User admin = currentUserResolver.getCurrentUser();
        return projectService.getPendingProjects(admin);
    }

    @PutMapping("/{id}/approve")
    public Project approve(@PathVariable Long id) {
        User admin = currentUserResolver.getCurrentUser();
        return projectService.approveProject(id, admin);
    }

    @PutMapping("/{id}/reject")
    public Project reject(@PathVariable Long id) {
        User admin = currentUserResolver.getCurrentUser();
        return projectService.rejectProject(id, admin);
    }

    // Admin, owner olmasa bile herhangi bir projeyi guncelleyebilir
    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project updated) {
        User admin = currentUserResolver.getCurrentUser();
        return projectService.adminUpdateProject(id, updated, admin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User admin = currentUserResolver.getCurrentUser();
        projectService.adminDeleteProject(id, admin);
        return ResponseEntity.noContent().build();
    }

    // Admin, belirtilen kullanici adina dogrudan (moderasyonsuz) proje olusturur
    @PostMapping
    public Project create(@RequestBody AdminCreateProjectRequest request) {
        User admin = currentUserResolver.getCurrentUser();
        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setCoverImageUrl(request.getCoverImageUrl());
        return projectService.adminCreateProject(project, request.getOwnerId(), admin);
    }
}
