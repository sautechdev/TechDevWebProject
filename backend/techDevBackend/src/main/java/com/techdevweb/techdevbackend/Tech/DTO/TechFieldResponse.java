package com.techdevweb.techdevbackend.Tech.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechFieldResponse {
    private Long id;
    private String name;
    private String icon;
    private String description;
}
