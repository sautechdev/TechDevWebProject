package com.techdevweb.techdevbackend.Tech.Mapper;

import com.techdevweb.techdevbackend.Tech.DTO.TechStackRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechStackResponse;
import com.techdevweb.techdevbackend.Tech.Entity.TechField;
import com.techdevweb.techdevbackend.Tech.Entity.TechStack;
import org.springframework.stereotype.Component;

@Component
public class TechStackMapper {

    public TechStack toEntity(TechStackRequest request, TechField techField) {
        return TechStack.builder()
                .name(request.getName())
                .logoUrl(request.getLogoUrl())
                .techField(techField)
                .build();
    }

    public TechStackResponse toResponse(TechStack entity) {
        return TechStackResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .logoUrl(entity.getLogoUrl())
                .fieldId(entity.getTechField().getId())
                .fieldName(entity.getTechField().getName())
                .build();
    }
}
