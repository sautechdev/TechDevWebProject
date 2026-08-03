package com.techdevweb.techdevbackend.Event.Service;

import com.techdevweb.techdevbackend.Event.DTO.EventRegistrationResponse;

import java.util.List;

public interface EventRegistrationService {
    EventRegistrationResponse register(Long eventId);
    void unregister(Long eventId);
    List<EventRegistrationResponse> getByEventId(Long eventId);
    EventRegistrationResponse approve(Long eventId, Long registrationId);
    EventRegistrationResponse reject(Long eventId, Long registrationId);
}
