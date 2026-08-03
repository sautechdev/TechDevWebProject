package com.techdevweb.techdevbackend.Tech.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechStackRequest {

    @NotBlank(message = "İsim boş olamaz")
    @Size(max = 100, message = "İsim 100 karakterden uzun olamaz")
    private String name;
    private String logoUrl;

    @NotNull(message = "Field ID zorunludur")
    private Long fieldId;
}
