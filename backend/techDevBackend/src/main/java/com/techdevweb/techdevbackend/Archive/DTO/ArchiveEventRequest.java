package com.techdevweb.techdevbackend.Archive.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveEventRequest {

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 200, message = "Başlık 200 karakterden uzun olamaz")
    private String title;

    @Size(max = 2000, message = "Açıklama 2000 karakterden uzun olamaz")
    private String description;

    @NotNull(message = "Etkinlik tarihi zorunludur")
    @PastOrPresent(message = "Arşiv tarihi gelecekte olamaz")
    private LocalDate eventDate;
}
