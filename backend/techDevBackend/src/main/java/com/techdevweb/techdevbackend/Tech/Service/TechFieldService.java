package com.techdevweb.techdevbackend.Tech.Service;

import com.techdevweb.techdevbackend.Tech.DTO.TechFieldRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechFieldResponse;

import java.util.List;

public interface TechFieldService {
    TechFieldResponse create(TechFieldRequest request);
    List<TechFieldResponse> getAll();
    TechFieldResponse getById(Long id);
    TechFieldResponse update(Long id, TechFieldRequest request);
    void delete(Long id);
}
