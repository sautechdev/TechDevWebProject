package com.techdevweb.techdevbackend.Event.DTO;

import com.techdevweb.techdevbackend.Event.Enum.EventPlatform;
import com.techdevweb.techdevbackend.Event.Enum.EventStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private EventPlatform platform;
    private String meetingLink;
    private String coverImageUrl;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer capacity;
    private int registeredCount;
    private boolean full;
    private EventStatus status; //otomatik hesaplanan alan
    private boolean requiresApproval;
}
