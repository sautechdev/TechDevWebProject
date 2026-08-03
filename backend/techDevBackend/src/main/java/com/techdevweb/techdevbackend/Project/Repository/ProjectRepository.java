package com.techdevweb.techdevbackend.Project.Repository;

import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Enum.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Projeler listesi sayfasi icin: statuye gore filtreli, sayfali liste
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    // Basliga gore arama (kucuk/buyuk harf duyarsiz)
    Page<Project> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
