package com.techdevweb.techdevbackend.Project.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ProjectExpertiseArea;

import java.util.List;

public interface ProjectExpertiseAreaService {

    List<ProjectExpertiseArea> getByProject(Long projectId);

    // Sadece proje sahibi ekleyebilir
    ProjectExpertiseArea addExpertiseArea(Long projectId, User currentUser, ProjectExpertiseArea area);

    ProjectExpertiseArea updateExpertiseArea(Long areaId, User currentUser, ProjectExpertiseArea updated);

    void deleteExpertiseArea(Long areaId, User currentUser);
}
