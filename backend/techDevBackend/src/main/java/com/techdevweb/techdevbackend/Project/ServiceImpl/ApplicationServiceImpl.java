package com.techdevweb.techdevbackend.Project.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Exception.ConflictException;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import com.techdevweb.techdevbackend.Notification.Service.NotificationService;
import com.techdevweb.techdevbackend.Project.Entity.Application;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Entity.ProjectExpertiseArea;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;
import com.techdevweb.techdevbackend.Project.Enum.ApplicationStatus;
import com.techdevweb.techdevbackend.Project.Enum.ProjectMemberRole;
import com.techdevweb.techdevbackend.Project.Repository.ApplicationRepository;
import com.techdevweb.techdevbackend.Project.Repository.ProjectExpertiseAreaRepository;
import com.techdevweb.techdevbackend.Project.Repository.ProjectMemberRepository;
import com.techdevweb.techdevbackend.Project.Service.ApplicationService;
import com.techdevweb.techdevbackend.Project.Service.ProjectAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectExpertiseAreaRepository expertiseAreaRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAccessGuard accessGuard;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Application apply(Long expertiseAreaId, User applicant, String message) {
        ProjectExpertiseArea area = expertiseAreaRepository.findById(expertiseAreaId)
                .orElseThrow(() -> new ResourceNotFoundException("Uzmanlik alani bulunamadi: " + expertiseAreaId));

        if (applicationRepository.existsByApplicantAndExpertiseArea(applicant, area)) {
            throw new ConflictException("Bu alana zaten basvurdunuz");
        }

        Application application = new Application();
        application.setApplicant(applicant);
        application.setExpertiseArea(area);
        application.setMessage(message);
        application.setStatus(ApplicationStatus.PENDING);
        Application saved = applicationRepository.save(application);

        // Proje sahibine "yeni başvuru var" bildirimi
        Project project = area.getProject();
        projectMemberRepository.findByProject_IdAndRole(project.getId(), ProjectMemberRole.OWNER)
                .ifPresent(ownerMembership -> notificationService.notify(
                        ownerMembership.getUser(),
                        NotificationType.APPLICATION_RECEIVED,
                        "Yeni Proje Başvurusu",
                        applicant.getFullName() + " kişisi \"" + project.getTitle() + "\" projenize başvurdu.",
                        project.getId()
                ));

        return saved;
    }

    @Override
    public List<Application> getMyApplications(User user) {
        return applicationRepository.findByApplicant(user);
    }

    @Override
    public List<Application> getApplicationsForProject(Long projectId, User currentUser) {
        accessGuard.requireOwner(projectId, currentUser);
        return applicationRepository.findByExpertiseArea_Project_Id(projectId);
    }

    @Override
    @Transactional
    public Application approve(Long applicationId, User currentUser) {
        Application application = getApplicationOrThrow(applicationId);
        Long projectId = application.getExpertiseArea().getProject().getId();
        accessGuard.requireOwner(projectId, currentUser);

        application.setStatus(ApplicationStatus.APPROVED);
        application.setDecidedAt(LocalDateTime.now());
        applicationRepository.save(application);

        ProjectExpertiseArea area = application.getExpertiseArea();
        area.setFilledCount(area.getFilledCount() + 1);
        expertiseAreaRepository.save(area);

        ProjectMember member = new ProjectMember();
        member.setProject(area.getProject());
        member.setUser(application.getApplicant());
        member.setExpertiseArea(area);
        member.setRole(ProjectMemberRole.MEMBER);
        projectMemberRepository.save(member);

        // Başvurana "kabul edildin" bildirimi
        notificationService.notify(
                application.getApplicant(),
                NotificationType.APPLICATION_ACCEPTED,
                "Başvurunuz Kabul Edildi",
                "\"" + area.getProject().getTitle() + "\" projesine katılım başvurunuz onaylandı.",
                area.getProject().getId()
        );

        return application;
    }

    @Override
    @Transactional
    public Application reject(Long applicationId, User currentUser) {
        Application application = getApplicationOrThrow(applicationId);
        Long projectId = application.getExpertiseArea().getProject().getId();
        accessGuard.requireOwner(projectId, currentUser);

        application.setStatus(ApplicationStatus.REJECTED);
        application.setDecidedAt(LocalDateTime.now());
        Application saved = applicationRepository.save(application);

        // Başvurana "reddedildin" bildirimi
        notificationService.notify(
                application.getApplicant(),
                NotificationType.APPLICATION_REJECTED,
                "Başvurunuz Reddedildi",
                "\"" + application.getExpertiseArea().getProject().getTitle() + "\" projesine katılım başvurunuz maalesef reddedildi.",
                application.getExpertiseArea().getProject().getId()
        );

        return saved;
    }

    private Application getApplicationOrThrow(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new com.techdevweb.techdevbackend.Exception.ResourceNotFoundException("Basvuru bulunamadi: " + id));
    }
}
