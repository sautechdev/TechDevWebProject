package com.techdevweb.techdevbackend.Notification.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationPreferenceRequest;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationPreferenceResponse;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationResponse;
import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import com.techdevweb.techdevbackend.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    // Diğer servislerin çağıracağı ana metot
    void notify(User user, NotificationType type, String title, String message, Long relatedEntityId);

    PageResponse<NotificationResponse> getMyNotifications(boolean onlyUnread, Pageable pageable);

    void markAsRead(Long notificationId);
    void markAllAsRead();
    void delete(Long notificationId);

    NotificationPreferenceResponse getPreference();
    NotificationPreferenceResponse updatePreference(NotificationPreferenceRequest request);
}
