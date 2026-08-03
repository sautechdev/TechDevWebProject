package com.techdevweb.techdevbackend.Admin.DTO;

import com.techdevweb.techdevbackend.Enum.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateUserRequest {
    private String email;
    private String password;
    private String fullName;
    private UserRole role; // ADMIN veya USER - admin bilerek burada rol atayabilir
}
