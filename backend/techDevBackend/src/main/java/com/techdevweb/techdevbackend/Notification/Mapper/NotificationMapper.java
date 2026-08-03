package com.techdevweb.techdevbackend.Notification.Mapper;

import com.techdevweb.techdevbackend.Notification.DTO.NotificationPreferenceResponse;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationResponse;
import com.techdevweb.techdevbackend.Notification.Entity.Notification;
import com.techdevweb.techdevbackend.Notification.Entity.NotificationPreference;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .relatedEntityId(entity.getRelatedEntityId())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationPreferenceResponse toResponse(NotificationPreference entity) {
        return NotificationPreferenceResponse.builder()
                .emailEnabled(entity.isEmailEnabled())
                .inAppEnabled(entity.isInAppEnabled())
                .build();
    }
}
