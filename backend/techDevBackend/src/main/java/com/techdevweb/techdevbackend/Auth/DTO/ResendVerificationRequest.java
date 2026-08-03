package com.techdevweb.techdevbackend.Auth.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendVerificationRequest {
    private String email;
}
