package com.techdevweb.techdevbackend.Project.Repository;

import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Entity.ProjectExpertiseArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectExpertiseAreaRepository extends JpaRepository<ProjectExpertiseArea, Long> {

    // Proje detay sayfasinda "aranan uzmanlik alanlari" listesi icin
    List<ProjectExpertiseArea> findByProject(Project project);

    List<ProjectExpertiseArea> findByProjectId(Long projectId);
}
