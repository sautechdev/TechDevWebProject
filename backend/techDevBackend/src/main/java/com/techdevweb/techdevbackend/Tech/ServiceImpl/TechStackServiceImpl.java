package com.techdevweb.techdevbackend.Tech.ServiceImpl;

import com.techdevweb.techdevbackend.Tech.DTO.TechStackRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechStackResponse;
import com.techdevweb.techdevbackend.Tech.Entity.TechField;
import com.techdevweb.techdevbackend.Tech.Entity.TechStack;
import com.techdevweb.techdevbackend.Tech.Mapper.TechStackMapper;
import com.techdevweb.techdevbackend.Tech.Repository.TechFieldRepository;
import com.techdevweb.techdevbackend.Tech.Repository.TechStackRepository;
import com.techdevweb.techdevbackend.Tech.Service.TechStackService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "techStacks")
public class TechStackServiceImpl implements TechStackService {

    private final TechStackRepository repository;
    private final TechFieldRepository fieldRepository;
    private final TechStackMapper mapper;

    @Override
    @CacheEvict(allEntries = true)
    public TechStackResponse create(TechStackRequest request) {
        TechField field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("TechField bulunamadı: " + request.getFieldId()));
        TechStack entity = mapper.toEntity(request, field);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Cacheable(key = "#fieldId")
    public List<TechStackResponse> getByFieldId(Long fieldId) {
        return repository.findByTechFieldId(fieldId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Cacheable(key = "#id")
    public TechStackResponse getById(Long id) {
        TechStack entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TechStack bulunamadı: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public TechStackResponse update(Long id, TechStackRequest request) {
        TechStack entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TechStack bulunamadı: " + id));
        TechField field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("TechField bulunamadı: " + request.getFieldId()));
        entity.setName(request.getName());
        entity.setLogoUrl(request.getLogoUrl());
        entity.setTechField(field);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<TechStackResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>();
        }

        return repository.searchByName(keyword.trim())
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
