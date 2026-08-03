package com.techdevweb.techdevbackend.Notification.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationPreferenceRequest;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationPreferenceResponse;
import com.techdevweb.techdevbackend.Notification.DTO.NotificationResponse;
import com.techdevweb.techdevbackend.Notification.Entity.Notification;
import com.techdevweb.techdevbackend.Notification.Entity.NotificationPreference;
import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import com.techdevweb.techdevbackend.Notification.Mapper.NotificationMapper;
import com.techdevweb.techdevbackend.Notification.Repository.NotificationPreferenceRepository;
import com.techdevweb.techdevbackend.Notification.Repository.NotificationRepository;
import com.techdevweb.techdevbackend.Notification.Service.MailService;
import com.techdevweb.techdevbackend.Notification.Service.NotificationService;
import com.techdevweb.techdevbackend.PageResponse;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationMapper mapper;
    private final MailService mailService;
    private final CurrentUserResolver currentUserResolver;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notify(User user, NotificationType type, String title, String message, Long relatedEntityId) {
        NotificationPreference preference = getOrCreatePreference(user);

        if (preference.isInAppEnabled()) {
            Notification notification = Notification.builder()
                    .user(user)
                    .type(type)
                    .title(title)
                    .message(message)
                    .relatedEntityId(relatedEntityId)
                    .build();
            Notification saved = notificationRepository.save(notification);

            // WebSocket üzerinden anlık push — kullanıcı o an bağlıysa hemen görür
            NotificationResponse response = mapper.toResponse(saved);
            messagingTemplate.convertAndSendToUser(
                    user.getEmail(), // Principal adı olarak email kullanıyoruz (JwtAuthenticationFilter'daki gibi)
                    "/queue/notifications",
                    response
            );
        }

        if (preference.isEmailEnabled()) {
            mailService.send(user.getEmail(), title, message);
        }
    }

    @Override
    public PageResponse<NotificationResponse> getMyNotifications(boolean onlyUnread, Pageable pageable) {
        User currentUser = currentUserResolver.getCurrentUser();

        Page<Notification> notificationPage = notificationRepository
                .findByUserWithFilter(currentUser.getId(), onlyUnread, pageable);

        List<NotificationResponse> content = notificationPage.getContent().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toCollection(ArrayList::new));

        return PageResponse.<NotificationResponse>builder()
                .content(content)
                .pageNumber(notificationPage.getNumber())
                .pageSize(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .last(notificationPage.isLast())
                .build();
    }

    @Override
    public void markAsRead(Long notificationId) {
        User currentUser = currentUserResolver.getCurrentUser();
        Notification notification = findOwnedNotificationOrThrow(notificationId, currentUser.getId());
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        User currentUser = currentUserResolver.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void delete(Long notificationId) {
        User currentUser = currentUserResolver.getCurrentUser();
        Notification notification = findOwnedNotificationOrThrow(notificationId, currentUser.getId());
        notificationRepository.delete(notification);
    }

    @Override
    public NotificationPreferenceResponse getPreference() {
        User currentUser = currentUserResolver.getCurrentUser();
        NotificationPreference preference = getOrCreatePreference(currentUser);
        return mapper.toResponse(preference);
    }

    @Override
    public NotificationPreferenceResponse updatePreference(NotificationPreferenceRequest request) {
        User currentUser = currentUserResolver.getCurrentUser();
        NotificationPreference preference = getOrCreatePreference(currentUser);

        preference.setEmailEnabled(request.isEmailEnabled());
        preference.setInAppEnabled(request.isInAppEnabled());

        NotificationPreference saved = preferenceRepository.save(preference);
        return mapper.toResponse(saved);
    }

    // Kullanıcının tercihi yoksa (ilk kez), varsayılan değerlerle oluştur
    private NotificationPreference getOrCreatePreference(User user) {
        return preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> preferenceRepository.save(
                        NotificationPreference.builder()
                                .user(user)
                                .emailEnabled(true)
                                .inAppEnabled(true)
                                .build()
                ));
    }

    private Notification findOwnedNotificationOrThrow(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Bildirim bulunamadı: " + notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Bu bildirim size ait değil.");
        }

        return notification;
    }
}
