package com.techdevweb.techdevbackend.Tech.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechContentResponse {
    private Long id;
    private Long stackId;
    private String stackName;
    private String wikipediaSummary;
    private String devtoArticles;
    private String githubRepos;
    private String customNotes;
    private String relatedCourses;
    private String relatedProjects;
}
