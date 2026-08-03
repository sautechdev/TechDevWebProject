package com.techdevweb.techdevbackend.Auth.DTO;

import com.techdevweb.techdevbackend.Enum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private UserRole role;
}
