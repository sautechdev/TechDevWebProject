package com.techdevweb.techdevbackend.Project.Repository;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;
import com.techdevweb.techdevbackend.Project.Enum.ProjectMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    // RBAC kontrolu icin ana metod: bu kullanici bu projede mi, rolu ne
    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    // Ayni kontrol, sadece projectId elimizdeyse (Project nesnesini ayrica cekmeye gerek kalmadan)
    Optional<ProjectMember> findByProject_IdAndUser(Long projectId, User user);

    Optional<ProjectMember> findByProject_IdAndRole(Long projectId, ProjectMemberRole role);

    // Proje calisma alanindaki uye listesi (owner dahil)
    List<ProjectMember> findByProject(Project project);

    List<ProjectMember> findByProject_Id(Long projectId);

    // Profildeki "Uye oldugum projeler" sekmesi icin
    List<ProjectMember> findByUser(User user);

    // Profildeki "Olusturdugum projeler" sekmesi icin (role=OWNER olanlar)
    List<ProjectMember> findByUserAndRole(User user, ProjectMemberRole role);

    boolean existsByProjectAndUser(Project project, User user);
}
