package com.techdevweb.techdevbackend.Tech.Mapper;

import com.techdevweb.techdevbackend.Tech.DTO.TechFieldRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechFieldResponse;
import com.techdevweb.techdevbackend.Tech.Entity.TechField;
import org.springframework.stereotype.Component;

@Component
public class TechFieldMapper {

    public TechField toEntity(TechFieldRequest request) {
        return TechField.builder()
                .name(request.getName())
                .icon(request.getIcon())
                .description(request.getDescription())
                .build();
    }

    public TechFieldResponse toResponse(TechField entity) {
        return TechFieldResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .description(entity.getDescription())
                .build();
    }
}


