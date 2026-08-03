package com.techdevweb.techdevbackend.Archive.DTO;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveEventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private List<ArchiveItemResponse> items;
}
