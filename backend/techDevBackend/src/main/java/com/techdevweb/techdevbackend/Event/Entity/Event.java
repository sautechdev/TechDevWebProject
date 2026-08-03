package com.techdevweb.techdevbackend.Event.Entity;

import com.techdevweb.techdevbackend.Event.Enum.EventPlatform;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventPlatform platform;

    @Column(nullable = false)
    private String meetingLink;

    private String coverImageUrl;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private Integer capacity; // null = sınırsız

    @Builder.Default
    @Column(nullable = false)
    private boolean cancelled = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EventRegistration> registrations = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean requiresApproval = false; // varsayılan: onay gerekmez

    @Builder.Default
    @Column(nullable = false)
    private boolean reminderSent = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
