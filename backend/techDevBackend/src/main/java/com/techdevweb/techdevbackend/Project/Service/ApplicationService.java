package com.techdevweb.techdevbackend.Project.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Application;

import java.util.List;

public interface ApplicationService {

    // Basvuru formu submit edildiginde cagrilir
    Application apply(Long expertiseAreaId, User applicant, String message);

    // Profildeki "Basvurdugum projeler" sekmesi
    List<Application> getMyApplications(User user);

    // Proje sahibinin gordugu, o projeye ait tum basvurular
    List<Application> getApplicationsForProject(Long projectId, User currentUser);

    // Onaylama: basvuruyu APPROVED yapar, ProjectMember olarak ekler, filledCount'u artirir
    Application approve(Long applicationId, User currentUser);

    Application reject(Long applicationId, User currentUser);
}
