package com.techdevweb.techdevbackend.Event.Mapper;

import com.techdevweb.techdevbackend.Event.DTO.EventRegistrationResponse;
import com.techdevweb.techdevbackend.Event.DTO.EventResponse;
import org.springframework.stereotype.Component;

import com.techdevweb.techdevbackend.Event.Entity.Event;
import com.techdevweb.techdevbackend.Event.Entity.EventRegistration;
import com.techdevweb.techdevbackend.Event.Enum.EventStatus;


import java.time.LocalDateTime;

@Component
public class EventMapper {

    public EventResponse toResponse(Event event, long confirmedCount, boolean canSeeLink) {
        EventStatus status = calculateStatus(event);
        boolean isFull = event.getCapacity() != null && confirmedCount >= event.getCapacity();

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .platform(event.getPlatform())
                .meetingLink(canSeeLink ? event.getMeetingLink() : null)
                .coverImageUrl(event.getCoverImageUrl())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .capacity(event.getCapacity())
                .registeredCount((int) confirmedCount)
                .full(isFull)
                .status(status)
                .requiresApproval(event.isRequiresApproval())
                .build();
    }

    public EventRegistrationResponse toRegistrationResponse(EventRegistration registration) {
        return EventRegistrationResponse.builder()
                .id(registration.getId())
                .userId(registration.getUser().getId())
                .userFullName(registration.getUser().getFullName())
                .userEmail(registration.getUser().getEmail())
                .registeredAt(registration.getRegisteredAt())
                .status(registration.getStatus())
                .build();
    }

    private EventStatus calculateStatus(Event event) {
        if (event.isCancelled()) {
            return EventStatus.CANCELLED;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(event.getStartDateTime())) {
            return EventStatus.UPCOMING;
        } else if (now.isAfter(event.getEndDateTime())) {
            return EventStatus.COMPLETED;
        } else {
            return EventStatus.ONGOING;
        }
    }
}
