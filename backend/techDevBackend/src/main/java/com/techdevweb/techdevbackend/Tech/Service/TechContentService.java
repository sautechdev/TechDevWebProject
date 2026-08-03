package com.techdevweb.techdevbackend.Tech.Service;

import com.techdevweb.techdevbackend.Tech.DTO.TechContentRequest;
import com.techdevweb.techdevbackend.Tech.DTO.TechContentResponse;

public interface TechContentService {
    TechContentResponse getByStackId(Long stackId);
    TechContentResponse updateCustomFields(Long stackId, TechContentRequest request);
}
