package com.techdevweb.techdevbackend.Tech.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechFieldRequest {

    @NotBlank(message = "İsim boş olamaz")
    @Size(max = 100, message = "İsim 100 karakterden uzun olamaz")
    private String name;

    @Size(max = 10, message = "İkon çok uzun")
    private String icon;

    @Size(max = 1000, message = "Açıklama 1000 karakterden uzun olamaz")
    private String description;
}
