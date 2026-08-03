package com.techdevweb.techdevbackend.Project.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;
import com.techdevweb.techdevbackend.Project.Enum.ProjectMemberRole;
import com.techdevweb.techdevbackend.Project.Repository.ProjectMemberRepository;
import com.techdevweb.techdevbackend.Project.Service.ProjectAccessGuard;
import com.techdevweb.techdevbackend.Project.Service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAccessGuard accessGuard;

    @Override
    public List<ProjectMember> getMembers(Long projectId) {
        return projectMemberRepository.findByProject_Id(projectId);
    }

    @Override
    @Transactional
    public void removeMember(Long projectId, Long memberId, User currentUser) {
        accessGuard.requireOwner(projectId, currentUser);

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new com.techdevweb.techdevbackend.Exception.ResourceNotFoundException("Uye bulunamadi: " + memberId));

        if (member.getRole() == ProjectMemberRole.OWNER) {
            throw new com.techdevweb.techdevbackend.Exception.ConflictException("Proje sahibi projeden cikarilamaz");
        }

        projectMemberRepository.delete(member);
    }
}
