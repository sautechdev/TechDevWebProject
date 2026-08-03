package com.techdevweb.techdevbackend.Tech.ServiceImpl;

import com.techdevweb.techdevbackend.Tech.DTO.TechContentRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechContentResponse;
import com.techdevweb.techdevbackend.Tech.Entity.TechContent;
import com.techdevweb.techdevbackend.Tech.Mapper.TechContentMapper;
import com.techdevweb.techdevbackend.Tech.Repository.TechContentRepository;
import com.techdevweb.techdevbackend.Tech.Service.TechContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "techContents")
public class TechContentServiceImpl implements TechContentService {

    private final TechContentRepository repository;
    private final TechContentMapper mapper;

    @Override
    @Cacheable(key = "#stackId")
    public TechContentResponse getByStackId(Long stackId) {
        TechContent content = repository.findByTechStackId(stackId)
                .orElseThrow(() -> new RuntimeException("İçerik bulunamadı: " + stackId));
        return mapper.toResponse(content);
    }

    @Override
    @CacheEvict(key = "#stackId")
    public TechContentResponse updateCustomFields(Long stackId, TechContentRequest request) {
        TechContent content = repository.findByTechStackId(stackId)
                .orElseThrow(() -> new RuntimeException("İçerik bulunamadı: " + stackId));
        content.setCustomNotes(request.getCustomNotes());
        content.setRelatedCourses(request.getRelatedCourses());
        content.setRelatedProjects(request.getRelatedProjects());
        return mapper.toResponse(repository.save(content));
    }
}
