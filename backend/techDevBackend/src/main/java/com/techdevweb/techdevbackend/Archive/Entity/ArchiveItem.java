package com.techdevweb.techdevbackend.Archive.Entity;

import com.techdevweb.techdevbackend.Archive.Enum.ArchiveItemType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archive_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchiveItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath; // /uploads/events/{eventId}/dosya.jpg

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArchiveItemType type;

    @Column
    private Long fileSize; // byte cinsinden

    @Column
    private String caption; // fotoğraf/video açıklaması (opsiyonel)

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_event_id", nullable = false)
    private ArchiveEvent archiveEvent;
}