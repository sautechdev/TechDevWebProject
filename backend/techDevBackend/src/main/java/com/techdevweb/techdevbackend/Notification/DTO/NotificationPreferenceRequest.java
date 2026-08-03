package com.techdevweb.techdevbackend.Notification.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceRequest {
    private boolean emailEnabled;
    private boolean inAppEnabled;
}
