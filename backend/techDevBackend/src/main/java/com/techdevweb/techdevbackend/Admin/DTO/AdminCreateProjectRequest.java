package com.techdevweb.techdevbackend.Admin.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateProjectRequest {
    private String title;
    private String description;
    private String coverImageUrl;
    private Long ownerId; // admin bu projeyi hangi kullanici adina olusturuyor
}
