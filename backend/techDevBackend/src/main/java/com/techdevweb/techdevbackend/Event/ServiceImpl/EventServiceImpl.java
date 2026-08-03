package com.techdevweb.techdevbackend.Event.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Event.DTO.EventRequest;
import com.techdevweb.techdevbackend.Event.DTO.EventResponse;
import com.techdevweb.techdevbackend.Event.Entity.Event;
import com.techdevweb.techdevbackend.Event.Entity.EventRegistration;
import com.techdevweb.techdevbackend.Event.Enum.EventStatus;
import com.techdevweb.techdevbackend.Event.Enum.RegistrationStatus;
import com.techdevweb.techdevbackend.Event.Mapper.EventMapper;
import com.techdevweb.techdevbackend.Event.Repository.EventRegistrationRepository;
import com.techdevweb.techdevbackend.Event.Repository.EventRepository;
import com.techdevweb.techdevbackend.Event.Service.EventService;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import com.techdevweb.techdevbackend.Notification.Service.NotificationService;
import com.techdevweb.techdevbackend.PageResponse;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.techdevweb.techdevbackend.Event.Enum.EventStatus.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventMapper mapper;
    private final CurrentUserResolver currentUserResolver;
    private final NotificationService notificationService;

    @Override
    public EventResponse create(EventRequest request) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .platform(request.getPlatform())
                .meetingLink(request.getMeetingLink())
                .coverImageUrl(request.getCoverImageUrl())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .capacity(request.getCapacity())
                .requiresApproval(request.isRequiresApproval())
                .cancelled(false)
                .build();

        Event saved = eventRepository.save(event);
        // Oluşturan zaten admin, linki görebilir
        return mapper.toResponse(saved, 0, true);
    }

    @Override
    public PageResponse<EventResponse> getAll(String keyword, EventStatus statusFilter, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        String searchKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        Page<Event> eventPage;

        if (statusFilter == null) {
            eventPage = eventRepository.search(searchKeyword, pageable);
        } else {
            eventPage = switch (statusFilter) {
                case UPCOMING -> eventRepository.findUpcoming(now, searchKeyword, pageable);
                case ONGOING -> eventRepository.findOngoing(now, searchKeyword, pageable);
                case COMPLETED -> eventRepository.findCompleted(now, searchKeyword, pageable);
                case CANCELLED -> eventRepository.findCancelled(searchKeyword, pageable);
            };
        }

        List<EventResponse> content = eventPage.getContent().stream()
                .map(event -> {
                    long count = registrationRepository.countByEventIdAndStatus(event.getId(), RegistrationStatus.CONFIRMED);
                    boolean canSeeLink = canUserSeeLink(event.getId());
                    return mapper.toResponse(event, count, canSeeLink);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        return PageResponse.<EventResponse>builder()
                .content(content)
                .pageNumber(eventPage.getNumber())
                .pageSize(eventPage.getSize())
                .totalElements(eventPage.getTotalElements())
                .totalPages(eventPage.getTotalPages())
                .last(eventPage.isLast())
                .build();
    }

    @Override
    public EventResponse getById(Long id) {
        Event event = findEventOrThrow(id);
        long count = registrationRepository.countByEventIdAndStatus(id, RegistrationStatus.CONFIRMED);
        boolean canSeeLink = canUserSeeLink(id);
        return mapper.toResponse(event, count, canSeeLink);
    }

    @Override
    public EventResponse update(Long id, EventRequest request) {
        Event event = findEventOrThrow(id);

        // Kritik alanlarda değişiklik var mı, güncellemeden ÖNCE kontrol et
        boolean criticalChange = !event.getStartDateTime().equals(request.getStartDateTime())
                || !event.getEndDateTime().equals(request.getEndDateTime())
                || !event.getMeetingLink().equals(request.getMeetingLink())
                || event.getPlatform() != request.getPlatform();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setPlatform(request.getPlatform());
        event.setMeetingLink(request.getMeetingLink());
        event.setCoverImageUrl(request.getCoverImageUrl());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setCapacity(request.getCapacity());
        event.setRequiresApproval(request.isRequiresApproval());

        Event saved = eventRepository.save(event);

        // Kritik bir değişiklik varsa, onaylı kayıtlı herkese bildirim gönder
        if (criticalChange) {
            List<EventRegistration> confirmedRegistrations = registrationRepository
                    .findByEventId(id)
                    .stream()
                    .filter(reg -> reg.getStatus() == RegistrationStatus.CONFIRMED)
                    .toList();

            confirmedRegistrations.forEach(reg ->
                    notificationService.notify(
                            reg.getUser(),
                            NotificationType.EVENT_UPDATED,
                            "Etkinlik Bilgileri Güncellendi",
                            saved.getTitle() + " etkinliğinin tarih, saat veya platform bilgisi değişti. Lütfen kontrol edin.",
                            saved.getId()
                    )
            );
        }

        long count = registrationRepository.countByEventIdAndStatus(id, RegistrationStatus.CONFIRMED);
        return mapper.toResponse(saved, count, true);
    }

    @Override
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Etkinlik bulunamadı: " + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public EventResponse cancel(Long id) {
        Event event = findEventOrThrow(id);
        event.setCancelled(true);
        Event saved = eventRepository.save(event);

        // Kayıtlı herkese bildirim gönder
        List<EventRegistration> registrations = registrationRepository.findByEventId(id);
        registrations.forEach(reg ->
                notificationService.notify(
                        reg.getUser(),
                        NotificationType.EVENT_CANCELLED,
                        "Etkinlik İptal Edildi",
                        event.getTitle() + " etkinliği iptal edildi.",
                        event.getId()
                )
        );

        long count = registrationRepository.countByEventIdAndStatus(id, RegistrationStatus.CONFIRMED);
        return mapper.toResponse(saved, count, true);
    }

    // Kullanıcı bu etkinliğin linkini görebilir mi?
    // - Giriş yapmamışsa: hayır
    // - Giriş yapmış ama kayıtlı değilse: hayır
    // - Kayıtlıysa: evet
    private boolean canUserSeeLink(Long eventId) {
        try {
            User currentUser = currentUserResolver.getCurrentUser();
            return registrationRepository.findByEventIdAndUserId(eventId, currentUser.getId())
                    .map(reg -> reg.getStatus() == RegistrationStatus.CONFIRMED)
                    .orElse(false);
        } catch (AccessDeniedException e) {
            return false;
        }
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etkinlik bulunamadı: " + id));
    }
}
