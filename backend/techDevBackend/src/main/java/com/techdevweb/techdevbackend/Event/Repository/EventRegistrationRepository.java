package com.techdevweb.techdevbackend.Event.Repository;

import com.techdevweb.techdevbackend.Event.Entity.EventRegistration;
import com.techdevweb.techdevbackend.Event.Enum.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEventId(Long eventId);
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    // kontenjan hesabı sadece ONAYLI kayıtları saymalı, PENDING'leri değil
    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);
}
