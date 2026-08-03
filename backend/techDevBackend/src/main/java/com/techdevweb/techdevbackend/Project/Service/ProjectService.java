package com.techdevweb.techdevbackend.Project.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import com.techdevweb.techdevbackend.Project.Enum.ProjectAccessRole;
import com.techdevweb.techdevbackend.Project.Enum.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    // Projeler listesi sayfasi (PUBLIC) - status filtresi opsiyonel.
    // PENDING/REJECTED projeler bu metoddan asla donmez (admin onayi bekleyen/reddedilen
    // projeler herkese acik listede gorunmemeli).
    Page<Project> getAllProjects(ProjectStatus status, Pageable pageable);

    // Proje detay sayfasi (PUBLIC). PENDING/REJECTED ise 404 gibi davranir.
    Project getProjectById(Long id);

    // Yeni proje olusturur (status=PENDING, admin onayi bekler) VE olusturan
    // kullaniciyi otomatik OWNER yapar
    Project createProject(Project project, User owner);

    Project updateProject(Long id, Project updated, User currentUser);

    void deleteProject(Long id, User currentUser);

    // GET /projects/{id}/my-role icin
    ProjectAccessRole getMyRole(Long projectId, User user);

    // Profildeki "Olusturdugum projeler" sekmesi - TUM durumlar dahil (PENDING/REJECTED de gorunur,
    // cunku sahibi kendi projesinin durumunu takip edebilmeli)
    List<Project> getMyProjects(User owner);

    // --- Admin metotlari (hepsi AdminAccessGuard ile korunur) ---

    // Admin panelindeki proje listesi - TUM durumlar dahil, status filtresi opsiyonel
    Page<Project> getAllProjectsAdmin(ProjectStatus status, Pageable pageable, User admin);

    List<Project> getPendingProjects(User admin);

    Project approveProject(Long id, User admin);

    Project rejectProject(Long id, User admin);

    // Admin, owner olmasa bile herhangi bir projeyi guncelleyebilir/silebilir
    Project adminUpdateProject(Long id, Project updated, User admin);

    void adminDeleteProject(Long id, User admin);

    // Admin, belirtilen kullanici adina dogrudan bir proje olusturabilir (status=ACTIVE, onay gerekmez)
    Project adminCreateProject(Project project, Long ownerId, User admin);
}
