package com.techdevweb.techdevbackend.Project.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Announcement;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Repository.AnnouncementRepository;
import com.techdevweb.techdevbackend.Project.Repository.ProjectRepository;
import com.techdevweb.techdevbackend.Project.Service.AnnouncementService;
import com.techdevweb.techdevbackend.Project.Service.ProjectAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAccessGuard accessGuard;

    @Override
    public List<Announcement> getAnnouncements(Long projectId) {
        Project project = getProjectOrThrow(projectId);
        return announcementRepository.findByProjectOrderByCreatedAtDesc(project);
    }

    @Override
    @Transactional
    public Announcement createAnnouncement(Long projectId, User currentUser, String title, String content) {
        accessGuard.requireOwner(projectId, currentUser);
        Project project = getProjectOrThrow(projectId);

        Announcement announcement = new Announcement();
        announcement.setProject(project);
        announcement.setAuthor(currentUser);
        announcement.setTitle(title);
        announcement.setContent(content);
        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId, User currentUser) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new com.techdevweb.techdevbackend.Exception.ResourceNotFoundException("Duyuru bulunamadi: " + announcementId));
        accessGuard.requireOwner(announcement.getProject().getId(), currentUser);
        announcementRepository.delete(announcement);
    }

    private Project getProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new com.techdevweb.techdevbackend.Exception.ResourceNotFoundException("Proje bulunamadi: " + id));
    }
}
