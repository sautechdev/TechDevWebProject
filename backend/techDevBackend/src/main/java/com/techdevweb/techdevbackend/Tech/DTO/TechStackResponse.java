package com.techdevweb.techdevbackend.Tech.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechStackResponse {
    private Long id;
    private String name;
    private String logoUrl;
    private Long fieldId;
    private String fieldName;
}
