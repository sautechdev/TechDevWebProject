package com.techdevweb.techdevbackend.Notification.Entity;

import com.techdevweb.techdevbackend.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private boolean emailEnabled = true; // varsayılan: email açık

    @Builder.Default
    @Column(nullable = false)
    private boolean inAppEnabled = true;
}
