package com.techdevweb.techdevbackend.Project.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;

import java.util.List;

public interface ProjectMemberService {

    List<ProjectMember> getMembers(Long projectId);

    // Sadece proje sahibi cikarabilir, OWNER cikarilamaz
    void removeMember(Long projectId, Long memberId, User currentUser);
}
