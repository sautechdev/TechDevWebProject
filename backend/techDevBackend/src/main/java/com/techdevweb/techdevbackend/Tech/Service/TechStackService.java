package com.techdevweb.techdevbackend.Tech.Service;

import com.techdevweb.techdevbackend.Tech.DTO.TechStackRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechStackResponse;

import java.util.List;

public interface TechStackService {
    TechStackResponse create(TechStackRequest request);
    List<TechStackResponse> getByFieldId(Long fieldId);
    TechStackResponse getById(Long id);
    TechStackResponse update(Long id, TechStackRequest request);
    void delete(Long id);
    List<TechStackResponse> search(String keyword);
}
