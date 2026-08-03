package com.techdevweb.techdevbackend.Notification.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceResponse {
    private boolean emailEnabled;
    private boolean inAppEnabled;
}
