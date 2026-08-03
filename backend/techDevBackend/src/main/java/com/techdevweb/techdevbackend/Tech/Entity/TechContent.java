package com.techdevweb.techdevbackend.Tech.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tech_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stack_id", nullable = false)
    private TechStack techStack;

    // API'den gelen alanlar
    @Column(columnDefinition = "TEXT")
    private String wikipediaSummary;

    @Column(columnDefinition = "TEXT")
    private String devtoArticles; // JSON string

    @Column(columnDefinition = "TEXT")
    private String githubRepos;   // JSON string

    // Sizin eklediğiniz alanlar
    @Column(columnDefinition = "TEXT")
    private String customNotes;

    @Column(columnDefinition = "TEXT")
    private String relatedCourses;

    @Column(columnDefinition = "TEXT")
    private String relatedProjects;
}
