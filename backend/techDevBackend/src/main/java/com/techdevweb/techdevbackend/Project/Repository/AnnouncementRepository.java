package com.techdevweb.techdevbackend.Project.Repository;

import com.techdevweb.techdevbackend.Project.Entity.Announcement;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // En yeni duyuru en ustte
    List<Announcement> findByProjectOrderByCreatedAtDesc(Project project);
}
