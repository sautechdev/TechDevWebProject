package com.techdevweb.techdevbackend.Admin.DTO;

import com.techdevweb.techdevbackend.Enum.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {
    // Hepsi opsiyonel - sadece dolu gonderilen alanlar guncellenir
    private String fullName;
    private String email;
    private UserRole role;
}
