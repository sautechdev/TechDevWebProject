package com.techdevweb.techdevbackend.Event.DTO;

import com.techdevweb.techdevbackend.Event.Enum.RegistrationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private LocalDateTime registeredAt;
    private RegistrationStatus status;
}
