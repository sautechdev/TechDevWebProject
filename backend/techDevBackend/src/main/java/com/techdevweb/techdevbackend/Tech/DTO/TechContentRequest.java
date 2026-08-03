package com.techdevweb.techdevbackend.Tech.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TechContentRequest {

    @NotNull(message = "Stack ID zorunludur")
    private Long stackId;

    @Size(max = 3000, message = "Notlar 3000 karakterden uzun olamaz")
    private String customNotes;

    @Size(max = 1000, message = "İlgili dersler alanı 1000 karakterden uzun olamaz")
    private String relatedCourses;

    @Size(max = 1000, message = "İlgili projeler alanı 1000 karakterden uzun olamaz")
    private String relatedProjects;
}
