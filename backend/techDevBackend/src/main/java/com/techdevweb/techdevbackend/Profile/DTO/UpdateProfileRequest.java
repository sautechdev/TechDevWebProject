package com.techdevweb.techdevbackend.Profile.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
    // Sadece dolu gonderilen alanlar guncellenir
    private String fullName;

    // Sifre degistirmek istemiyorsa ikisini de bos birakabilir.
    // newPassword doluysa currentPassword da dogrulanmak zorunda.
    private String currentPassword;
    private String newPassword;
}
