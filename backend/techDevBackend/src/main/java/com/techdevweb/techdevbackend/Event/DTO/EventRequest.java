package com.techdevweb.techdevbackend.Event.DTO;

import com.techdevweb.techdevbackend.Event.Enum.EventPlatform;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 200, message = "Başlık 200 karakterden uzun olamaz")
    private String title;

    @Size(max = 2000, message = "Açıklama 2000 karakterden uzun olamaz")
    private String description;

    @NotNull(message = "Platform seçilmelidir")
    private EventPlatform platform;

    @NotBlank(message = "Toplantı linki boş olamaz")
    private String meetingLink;

    private String coverImageUrl;

    @NotNull(message = "Başlangıç tarihi zorunludur")
    @Future(message = "Başlangıç tarihi gelecekte olmalıdır")
    private LocalDateTime startDateTime;

    @NotNull(message = "Bitiş tarihi zorunludur")
    private LocalDateTime endDateTime;

    @Min(value = 1, message = "Kontenjan en az 1 olmalıdır")
    private Integer capacity;

    private boolean requiresApproval;
}