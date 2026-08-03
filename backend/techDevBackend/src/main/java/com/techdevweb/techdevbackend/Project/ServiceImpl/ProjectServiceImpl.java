package com.techdevweb.techdevbackend.Project.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import com.techdevweb.techdevbackend.Notification.Service.NotificationService;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;
import com.techdevweb.techdevbackend.Project.Enum.ProjectAccessRole;
import com.techdevweb.techdevbackend.Project.Enum.ProjectMemberRole;
import com.techdevweb.techdevbackend.Project.Enum.ProjectStatus;
import com.techdevweb.techdevbackend.Project.Repository.ApplicationRepository;
import com.techdevweb.techdevbackend.Project.Repository.ProjectMemberRepository;
import com.techdevweb.techdevbackend.Project.Repository.ProjectRepository;
import com.techdevweb.techdevbackend.Project.Service.ProjectAccessGuard;
import com.techdevweb.techdevbackend.Project.Service.ProjectService;
import com.techdevweb.techdevbackend.Repository.UserRepository;
import com.techdevweb.techdevbackend.Security.AdminAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ProjectAccessGuard accessGuard;
    private final AdminAccessGuard adminAccessGuard;
    private final NotificationService notificationService;

    @Override
    public Page<Project> getAllProjects(ProjectStatus status, Pageable pageable) {
        // Public listede PENDING/REJECTED asla gorunmez - biri bunu status parametresiyle
        // zorlamaya calisirsa bile sessizce ACTIVE'e dusuruyoruz.
        if (status == null || status == ProjectStatus.PENDING || status == ProjectStatus.REJECTED) {
            return projectRepository.findByStatus(ProjectStatus.ACTIVE, pageable);
        }
        return projectRepository.findByStatus(status, pageable);
    }

    @Override
    public Project getProjectById(Long id) {
        Project project = findProjectOrThrow(id);
        // Public detay sayfasi: onay bekleyen/reddedilen projeler "yokmus" gibi davranir.
        // Sahibi kendi PENDING projesini /users/me/projects uzerinden gorur, admin ise
        // /admin/projects uzerinden - bu endpoint sadece herkese acik olanlari gosterir.
        if (project.getStatus() == ProjectStatus.PENDING || project.getStatus() == ProjectStatus.REJECTED) {
            throw new ResourceNotFoundException("Proje bulunamadi: " + id);
        }
        return project;
    }

    @Override
    @Transactional
    public Project createProject(Project project, User owner) {
        return createProjectInternal(project, owner, ProjectStatus.PENDING);
    }

    @Override
    @Transactional
    public Project updateProject(Long id, Project updated, User currentUser) {
        Project existing = findProjectOrThrow(id);
        accessGuard.requireOwner(id, currentUser);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCoverImageUrl(updated.getCoverImageUrl());
        // Not: sahibi kendi projesinin durumunu (status) degistiremez - bu sadece admin'e ait,
        // yoksa biri PENDING projesini elle ACTIVE yapip moderasyonu atlayabilir.
        return projectRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProject(Long id, User currentUser) {
        Project existing = findProjectOrThrow(id);
        accessGuard.requireOwner(id, currentUser);
        projectRepository.delete(existing);
    }

    @Override
    public ProjectAccessRole getMyRole(Long projectId, User user) {
        return projectMemberRepository.findByProject_IdAndUser(projectId, user)
                .map(member -> member.getRole() == ProjectMemberRole.OWNER
                        ? ProjectAccessRole.OWNER
                        : ProjectAccessRole.MEMBER)
                .orElseGet(() -> {
                    boolean hasApplied = applicationRepository.findByApplicant(user).stream()
                            .anyMatch(app -> app.getExpertiseArea().getProject().getId().equals(projectId));
                    return hasApplied ? ProjectAccessRole.APPLICANT : ProjectAccessRole.NONE;
                });
    }

    @Override
    public List<Project> getMyProjects(User owner) {
        return projectMemberRepository.findByUserAndRole(owner, ProjectMemberRole.OWNER).stream()
                .map(ProjectMember::getProject)
                .collect(Collectors.toList());
    }

    // ------------------- Admin metotlari -------------------

    @Override
    public Page<Project> getAllProjectsAdmin(ProjectStatus status, Pageable pageable, User admin) {
        adminAccessGuard.requireAdmin();
        if (status != null) {
            return projectRepository.findByStatus(status, pageable);
        }
        return projectRepository.findAll(pageable);
    }

    @Override
    public List<Project> getPendingProjects(User admin) {
        adminAccessGuard.requireAdmin();
        return projectRepository.findByStatus(ProjectStatus.PENDING, Pageable.unpaged())
                .getContent();
    }

    @Override
    @Transactional
    public Project approveProject(Long id, User admin) {
        adminAccessGuard.requireAdmin();
        Project project = findProjectOrThrow(id);
        project.setStatus(ProjectStatus.ACTIVE);
        Project saved = projectRepository.save(project);

        // Proje sahibine bildirim
        projectMemberRepository.findByProject_IdAndRole(id, ProjectMemberRole.OWNER)
                .ifPresent(ownerMembership -> notificationService.notify(
                        ownerMembership.getUser(),
                        NotificationType.PROJECT_APPROVED,
                        "Projeniz Onaylandı",
                        "\"" + project.getTitle() + "\" projeniz incelendi ve yayına alındı.",
                        project.getId()
                ));

        return saved;
    }

    @Override
    @Transactional
    public Project rejectProject(Long id, User admin) {
        adminAccessGuard.requireAdmin();
        Project project = findProjectOrThrow(id);
        project.setStatus(ProjectStatus.REJECTED);
        Project saved = projectRepository.save(project);

        // Proje sahibine bildirim
        projectMemberRepository.findByProject_IdAndRole(id, ProjectMemberRole.OWNER)
                .ifPresent(ownerMembership -> notificationService.notify(
                        ownerMembership.getUser(),
                        NotificationType.PROJECT_REJECTED,
                        "Projeniz Reddedildi",
                        "\"" + project.getTitle() + "\" projeniz incelendi ve maalesef onaylanmadı.",
                        project.getId()
                ));

        return saved;
    }

    @Override
    @Transactional
    public Project adminUpdateProject(Long id, Project updated, User admin) {
        adminAccessGuard.requireAdmin();
        Project existing = findProjectOrThrow(id);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCoverImageUrl(updated.getCoverImageUrl());
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        return projectRepository.save(existing);
    }

    @Override
    @Transactional
    public void adminDeleteProject(Long id, User admin) {
        adminAccessGuard.requireAdmin();
        Project existing = findProjectOrThrow(id);
        projectRepository.delete(existing);
    }

    @Override
    @Transactional
    public Project adminCreateProject(Project project, Long ownerId, User admin) {
        adminAccessGuard.requireAdmin();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + ownerId));
        // Admin dogrudan olusturdugu icin moderasyon beklemez, direkt ACTIVE.
        return createProjectInternal(project, owner, ProjectStatus.ACTIVE);
    }

    // ------------------- Ortak yardimci metotlar -------------------

    private Project createProjectInternal(Project project, User owner, ProjectStatus initialStatus) {
        project.setStatus(initialStatus);
        Project saved = projectRepository.save(project);

        ProjectMember ownerMembership = new ProjectMember();
        ownerMembership.setProject(saved);
        ownerMembership.setUser(owner);
        ownerMembership.setRole(ProjectMemberRole.OWNER);
        projectMemberRepository.save(ownerMembership);

        return saved;
    }

    // Status filtresi uygulamadan ham fetch - update/delete/onay gibi ic islemler icin.
    // getProjectById(id) ile karistirmayin: o public-safe filtre uygular, bu uygulamaz.
    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proje bulunamadi: " + id));
    }
}
