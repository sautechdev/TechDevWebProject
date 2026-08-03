package com.techdevweb.techdevbackend.Archive.DTO;

import com.techdevweb.techdevbackend.Archive.Enum.ArchiveItemType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveItemResponse {
    private Long id;
    private String fileName;
    private String fileUrl; // frontend'in erişeceği tam URL
    private ArchiveItemType type;
    private Long fileSize;
    private String caption;
    private LocalDateTime uploadedAt;
    private Long archiveEventId;
}
