package com.techdevweb.techdevbackend.Event;

import com.techdevweb.techdevbackend.Event.Entity.Event;
import com.techdevweb.techdevbackend.Event.Entity.EventRegistration;
import com.techdevweb.techdevbackend.Event.Enum.RegistrationStatus;
import com.techdevweb.techdevbackend.Event.Repository.EventRegistrationRepository;
import com.techdevweb.techdevbackend.Event.Repository.EventRepository;
import com.techdevweb.techdevbackend.Notification.Enum.NotificationType;
import com.techdevweb.techdevbackend.Notification.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventReminderScheduler {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    // Her 5 dakikada bir çalışır
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void sendUpcomingEventReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusHours(1); // 1 saat içinde başlayacaklar

        List<Event> eventsNeedingReminder = eventRepository.findEventsNeedingReminder(now, windowEnd);

        if (eventsNeedingReminder.isEmpty()) {
            return;
        }

        log.info("{} etkinlik için hatırlatma gönderilecek", eventsNeedingReminder.size());

        for (Event event : eventsNeedingReminder) {
            List<EventRegistration> confirmedRegistrations = registrationRepository
                    .findByEventId(event.getId())
                    .stream()
                    .filter(reg -> reg.getStatus() == RegistrationStatus.CONFIRMED)
                    .toList();

            for (EventRegistration registration : confirmedRegistrations) {
                notificationService.notify(
                        registration.getUser(),
                        NotificationType.EVENT_REMINDER,
                        "Etkinlik Hatırlatması",
                        event.getTitle() + " etkinliği 1 saat içinde başlıyor!",
                        event.getId()
                );
            }

            // Aynı etkinlik için tekrar tekrar hatırlatma göndermemek adına flag'i işaretle
            event.setReminderSent(true);
            eventRepository.save(event);

            log.info("Hatırlatma gönderildi: {} ({} kişiye)", event.getTitle(), confirmedRegistrations.size());
        }
    }
}
