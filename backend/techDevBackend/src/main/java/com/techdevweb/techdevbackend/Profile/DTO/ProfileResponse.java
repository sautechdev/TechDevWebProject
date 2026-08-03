package com.techdevweb.techdevbackend.Profile.DTO;

import com.techdevweb.techdevbackend.Enum.UserRole;
import com.techdevweb.techdevbackend.Skill.Entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
    private LocalDateTime createdAt;
    private List<Skill> skills;
}
