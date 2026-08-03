package com.techdevweb.techdevbackend.Project.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;
import com.techdevweb.techdevbackend.Project.Enum.ProjectMemberRole;
import com.techdevweb.techdevbackend.Project.Repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

// Butun Project modulu servislerinin RBAC kontrolu icin kullandigi tek merkez.
// "Bu kullanici bu projede ne yapabilir" sorusunun cevabi hep buradan gecer.
@Component
@RequiredArgsConstructor
public class ProjectAccessGuard {

    private final ProjectMemberRepository projectMemberRepository;

    // Kullanicinin projede herhangi bir uyeligi var mi (OWNER veya MEMBER)?
    // Yoksa 403 firlatir.
    public ProjectMember requireMembership(Long projectId, User user) {
        return projectMemberRepository.findByProject_IdAndUser(projectId, user)
                .orElseThrow(() -> new AccessDeniedException("Bu projeye erisim yetkiniz yok"));
    }

    // Sadece OWNER gecebilir.
    public ProjectMember requireOwner(Long projectId, User user) {
        ProjectMember membership = requireMembership(projectId, user);
        if (membership.getRole() != ProjectMemberRole.OWNER) {
            throw new AccessDeniedException("Bu islemi sadece proje sahibi yapabilir");
        }
        return membership;
    }

    // Herhangi bir uye (OWNER dahil) yeterli - chat/duyuru okuma gibi islemler icin.
    public void requireMember(Long projectId, User user) {
        requireMembership(projectId, user);
    }
}
