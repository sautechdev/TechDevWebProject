package com.techdevweb.techdevbackend.Project.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Entity.ProjectExpertiseArea;
import com.techdevweb.techdevbackend.Project.Repository.ProjectExpertiseAreaRepository;
import com.techdevweb.techdevbackend.Project.Repository.ProjectRepository;
import com.techdevweb.techdevbackend.Project.Service.ProjectAccessGuard;
import com.techdevweb.techdevbackend.Project.Service.ProjectExpertiseAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectExpertiseAreaServiceImpl implements ProjectExpertiseAreaService {

    private final ProjectExpertiseAreaRepository areaRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAccessGuard accessGuard;

    @Override
    public List<ProjectExpertiseArea> getByProject(Long projectId) {
        return areaRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional
    public ProjectExpertiseArea addExpertiseArea(Long projectId, User currentUser, ProjectExpertiseArea area) {
        accessGuard.requireOwner(projectId, currentUser);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new com.techdevweb.techdevbackend.Exception.ResourceNotFoundException("Proje bulunamadi: " + projectId));

        area.setProject(project);
        area.setFilledCount(0);
        return areaRepository.save(area);
    }

    @Override
    @Transactional
    public ProjectExpertiseArea updateExpertiseArea(Long areaId, User currentUser, ProjectExpertiseArea updated) {
        ProjectExpertiseArea existing = getAreaOrThrow(areaId);
        accessGuard.requireOwner(existing.getProject().getId(), currentUser);

        existing.setTechField(updated.getTechField());
        existing.setRequiredCount(updated.getRequiredCount());
        existing.setNote(updated.getNote());
        return areaRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteExpertiseArea(Long areaId, User currentUser) {
        ProjectExpertiseArea existing = getAreaOrThrow(areaId);
        accessGuard.requireOwner(existing.getProject().getId(), currentUser);
        areaRepository.delete(existing);
    }

    private ProjectExpertiseArea getAreaOrThrow(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new com.techdevweb.techdevbackend.Exception.ResourceNotFoundException("Uzmanlik alani bulunamadi: " + id));
    }
}
