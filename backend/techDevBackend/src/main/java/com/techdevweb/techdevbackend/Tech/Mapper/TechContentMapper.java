package com.techdevweb.techdevbackend.Tech.Mapper;

import com.techdevweb.techdevbackend.Tech.DTO.TechContentResponse;
import com.techdevweb.techdevbackend.Tech.Entity.TechContent;
import org.springframework.stereotype.Component;

@Component
public class TechContentMapper {

    public TechContentResponse toResponse(TechContent entity) {
        return TechContentResponse.builder()
                .id(entity.getId())
                .stackId(entity.getTechStack().getId())
                .stackName(entity.getTechStack().getName())
                .wikipediaSummary(entity.getWikipediaSummary())
                .devtoArticles(entity.getDevtoArticles())
                .githubRepos(entity.getGithubRepos())
                .customNotes(entity.getCustomNotes())
                .relatedCourses(entity.getRelatedCourses())
                .relatedProjects(entity.getRelatedProjects())
                .build();
    }
}
