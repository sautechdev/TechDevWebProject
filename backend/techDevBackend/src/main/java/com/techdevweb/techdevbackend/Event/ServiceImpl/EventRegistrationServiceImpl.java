package com.techdevweb.techdevbackend.Event.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Event.DTO.EventRegistrationResponse;
import com.techdevweb.techdevbackend.Event.Entity.Event;
import com.techdevweb.techdevbackend.Event.Entity.EventRegistration;
import com.techdevweb.techdevbackend.Event.Enum.RegistrationStatus;
import com.techdevweb.techdevbackend.Event.Mapper.EventMapper;
import com.techdevweb.techdevbackend.Event.Repository.EventRegistrationRepository;
import com.techdevweb.techdevbackend.Event.Repository.EventRepository;
import com.techdevweb.techdevbackend.Event.Service.EventRegistrationService;
import com.techdevweb.techdevbackend.Exception.ConflictException;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import com.techdevweb.techdevbackend.Notification.Service.NotificationService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventMapper mapper;
    private final CurrentUserResolver currentUserResolver;
    private final NotificationService notificationService;

    @Override
    public EventRegistrationResponse register(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Etkinlik bulunamadı: " + eventId));

        User currentUser = currentUserResolver.getCurrentUser();

        if (event.isCancelled()) {
            throw new ConflictException("Bu etkinlik iptal edildiği için kayıt olunamaz.");
        }

        if (registrationRepository.existsByEventIdAndUserId(eventId, currentUser.getId())) {
            throw new ConflictException("Bu etkinliğe zaten kayıtlısınız (veya onay bekliyorsunuz).");
        }

        // Kontenjan kontrolü — sadece onaylı kayıtlar sayılır
        if (event.getCapacity() != null) {
            long confirmedCount = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED);
            if (confirmedCount >= event.getCapacity()) {
                throw new ConflictException("Bu etkinliğin kontenjanı dolmuştur.");
            }
        }

        // Etkinlik onay gerektiriyorsa PENDING, gerektirmiyorsa direkt CONFIRMED
        RegistrationStatus initialStatus = event.isRequiresApproval()
                ? RegistrationStatus.PENDING
                : RegistrationStatus.CONFIRMED;

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(currentUser)
                .status(initialStatus)
                .build();

        EventRegistration saved = registrationRepository.save(registration);
        if (initialStatus == RegistrationStatus.CONFIRMED) {
            notificationService.notify(
                    currentUser,
                    NotificationType.EVENT_REGISTRATION_CONFIRMED,
                    "Kayıt Onaylandı",
                    event.getTitle() + " etkinliğine kaydınız onaylandı.",
                    event.getId()
            );
        } else {
            notificationService.notify(
                    currentUser,
                    NotificationType.EVENT_REGISTRATION_PENDING,
                    "Kayıt Onay Bekliyor",
                    event.getTitle() + " etkinliğine kayıt talebiniz onay bekliyor.",
                    event.getId()
            );
        }

        return mapper.toRegistrationResponse(saved);
    }

    @Override
    public void unregister(Long eventId) {
        User currentUser = currentUserResolver.getCurrentUser();

        EventRegistration registration = registrationRepository
                .findByEventIdAndUserId(eventId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bu etkinliğe kayıtlı değilsiniz."));

        registrationRepository.delete(registration);
    }

    @Override
    public List<EventRegistrationResponse> getByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId)
                .stream()
                .map(mapper::toRegistrationResponse)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // YENİ — Admin onaylıyor
    @Override
    public EventRegistrationResponse approve(Long eventId, Long registrationId) {
        EventRegistration registration = getRegistrationOrThrow(eventId, registrationId);

        // Onaylamadan önce kontenjan tekrar kontrol edilmeli (yarışma durumu olmasın diye)
        Event event = registration.getEvent();
        if (event.getCapacity() != null) {
            long confirmedCount = registrationRepository.countByEventIdAndStatus(eventId, RegistrationStatus.CONFIRMED);
            if (confirmedCount >= event.getCapacity()) {
                throw new ConflictException("Kontenjan dolu, onaylanamaz.");
            }
        }

        registration.setStatus(RegistrationStatus.CONFIRMED);
        EventRegistration saved = registrationRepository.save(registration);

        notificationService.notify(
                registration.getUser(),
                NotificationType.EVENT_REGISTRATION_APPROVED,
                "Kaydınız Onaylandı",
                registration.getEvent().getTitle() + " etkinliğine katılımınız onaylandı.",
                registration.getEvent().getId()
        );
        return mapper.toRegistrationResponse(registrationRepository.save(registration));
    }

    // YENİ — Admin reddediyor
    @Override
    public EventRegistrationResponse reject(Long eventId, Long registrationId) {
        EventRegistration registration = getRegistrationOrThrow(eventId, registrationId);
        registration.setStatus(RegistrationStatus.REJECTED);
        EventRegistration saved = registrationRepository.save(registration);

        notificationService.notify(
                registration.getUser(),
                NotificationType.EVENT_REGISTRATION_REJECTED,
                "Kayıt Talebiniz Reddedildi",
                registration.getEvent().getTitle() + " etkinliğine katılım talebiniz maalesef reddedildi.",
                registration.getEvent().getId()
        );
        return mapper.toRegistrationResponse(registrationRepository.save(registration));
    }

    private EventRegistration getRegistrationOrThrow(Long eventId, Long registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Kayıt bulunamadı: " + registrationId));

        if (!registration.getEvent().getId().equals(eventId)) {
            throw new ResourceNotFoundException("Bu kayıt, belirtilen etkinliğe ait değil.");
        }

        return registration;
    }
}