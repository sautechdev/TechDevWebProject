package com.techdevweb.techdevbackend.Tech.ServiceImpl;

import com.techdevweb.techdevbackend.Tech.DTO.TechFieldRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechFieldResponse;
import com.techdevweb.techdevbackend.Tech.Entity.TechField;
import com.techdevweb.techdevbackend.Tech.Mapper.TechFieldMapper;
import com.techdevweb.techdevbackend.Tech.Repository.TechFieldRepository;
import com.techdevweb.techdevbackend.Tech.Service.TechFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "techFields")
public class TechFieldServiceImpl implements TechFieldService {

    private final TechFieldRepository repository;
    private final TechFieldMapper mapper;

    @Override
    @CacheEvict(allEntries = true)
    public TechFieldResponse create(TechFieldRequest request) {
        TechField entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Cacheable(key = "'all'")
    public List<TechFieldResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toCollection(ArrayList::new)); // 🔑 değişti
    }

    @Override
    @Cacheable(key = "#id")
    public TechFieldResponse getById(Long id) {
        TechField entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TechField bulunamadı: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public TechFieldResponse update(Long id, TechFieldRequest request) {
        TechField entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TechField bulunamadı: " + id));
        entity.setName(request.getName());
        entity.setIcon(request.getIcon());
        entity.setDescription(request.getDescription());
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
