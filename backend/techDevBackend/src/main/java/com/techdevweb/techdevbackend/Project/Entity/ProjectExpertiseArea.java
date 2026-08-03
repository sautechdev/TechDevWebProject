package com.techdevweb.techdevbackend.Project.Entity;

// TODO: TechField'ın gerçek paket yolunu doğrulayın
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.techdevweb.techdevbackend.Tech.Entity.TechField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "project_expertise_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectExpertiseArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // JsonIgnore: Project -> expertiseAreas -> project -> ... sonsuz donguyu onler
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_field_id", nullable = false)
    private TechField techField;

    // Integer (int degil): Jackson JSON'da bu alan eksikse null birakabilsin diye.
    // Servis katmaninda addExpertiseArea() zaten filledCount'u 0'a set ediyor.
    @Column(nullable = false)
    private Integer requiredCount;

    @Column(nullable = false)
    private Integer filledCount = 0;

    private String note;
}
