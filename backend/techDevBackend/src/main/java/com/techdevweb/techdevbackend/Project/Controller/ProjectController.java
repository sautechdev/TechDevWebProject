package com.techdevweb.techdevbackend.Project.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Enum.ProjectAccessRole;
import com.techdevweb.techdevbackend.Project.Enum.ProjectStatus;
import com.techdevweb.techdevbackend.Project.Service.ProjectService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final CurrentUserResolver currentUserResolver;

    // Projeler listesi sayfasi - genel/public, auth gerekmez
    @GetMapping
    public Page<Project> getAll(
            @RequestParam(required = false) ProjectStatus status,
            Pageable pageable) {
        return projectService.getAllProjects(status, pageable);
    }

    // Proje detay sayfasi - genel/public
    @GetMapping("/{id}")
    public Project getById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    public Project create(@RequestBody Project project) {
        User owner = currentUserResolver.getCurrentUser();
        return projectService.createProject(project, owner);
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project updated) {
        User currentUser = currentUserResolver.getCurrentUser();
        return projectService.updateProject(id, updated, currentUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User currentUser = currentUserResolver.getCurrentUser();
        projectService.deleteProject(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // Proje calisma alanina girerken frontend'in RBAC icin sordugu endpoint
    @GetMapping("/{id}/my-role")
    public ProjectAccessRole getMyRole(@PathVariable Long id) {
        User currentUser = currentUserResolver.getCurrentUser();
        return projectService.getMyRole(id, currentUser);
    }
}
