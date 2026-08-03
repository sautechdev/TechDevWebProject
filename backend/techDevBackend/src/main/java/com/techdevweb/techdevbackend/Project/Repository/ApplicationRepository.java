package com.techdevweb.techdevbackend.Project.Repository;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.Application;
import com.techdevweb.techdevbackend.Project.Entity.ProjectExpertiseArea;
import com.techdevweb.techdevbackend.Project.Enum.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Kullanicinin kendi basvurulari ("Basvurdugum projeler" sekmesi icin)
    List<Application> findByApplicant(User applicant);

    // Proje yoneticisinin gordugu, bir projeye ait tum basvurular
    List<Application> findByExpertiseArea_Project_Id(Long projectId);

    // Sadece bekleyen basvurular (onay/red ekrani icin)
    List<Application> findByExpertiseArea_Project_IdAndStatus(Long projectId, ApplicationStatus status);

    // Ayni kisi ayni alana iki kez basvurmasin diye kontrol
    boolean existsByApplicantAndExpertiseArea(User applicant, ProjectExpertiseArea expertiseArea);
}
