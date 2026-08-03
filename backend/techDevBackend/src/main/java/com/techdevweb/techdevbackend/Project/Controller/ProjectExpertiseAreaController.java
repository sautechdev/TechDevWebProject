package com.techdevweb.techdevbackend.Project.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ProjectExpertiseArea;
import com.techdevweb.techdevbackend.Project.Service.ProjectExpertiseAreaService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/expertise-areas")
@RequiredArgsConstructor
public class ProjectExpertiseAreaController {

    private final ProjectExpertiseAreaService areaService;
    private final CurrentUserResolver currentUserResolver;

    // Proje detay sayfasindaki "aranan uzmanlik alanlari" listesi - genel/public
    @GetMapping
    public List<ProjectExpertiseArea> getAll(@PathVariable Long projectId) {
        return areaService.getByProject(projectId);
    }

    @PostMapping
    public ProjectExpertiseArea add(@PathVariable Long projectId,
                                     @RequestBody ProjectExpertiseArea area) {
        User currentUser = currentUserResolver.getCurrentUser();
        return areaService.addExpertiseArea(projectId, currentUser, area);
    }

    @PutMapping("/{areaId}")
    public ProjectExpertiseArea update(@PathVariable Long projectId,
                                        @PathVariable Long areaId,
                                        @RequestBody ProjectExpertiseArea updated) {
        User currentUser = currentUserResolver.getCurrentUser();
        return areaService.updateExpertiseArea(areaId, currentUser, updated);
    }

    @DeleteMapping("/{areaId}")
    public ResponseEntity<Void> delete(@PathVariable Long projectId,
                                        @PathVariable Long areaId) {
        User currentUser = currentUserResolver.getCurrentUser();
        areaService.deleteExpertiseArea(areaId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
