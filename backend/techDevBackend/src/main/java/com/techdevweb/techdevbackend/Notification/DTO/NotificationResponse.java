package com.techdevweb.techdevbackend.Notification.DTO;

import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Long relatedEntityId;
    private boolean read;
    private LocalDateTime createdAt;
}
