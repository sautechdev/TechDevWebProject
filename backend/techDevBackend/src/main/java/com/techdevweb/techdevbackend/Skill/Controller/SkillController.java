package com.techdevweb.techdevbackend.Skill.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import com.techdevweb.techdevbackend.Skill.Entity.Skill;
import com.techdevweb.techdevbackend.Skill.Service.SkillService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final CurrentUserResolver currentUserResolver;

    // Herkese acik - yetkinlik katalogu (dropdown/secim listesi icin)
    @GetMapping("/api/skills")
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    // Admin: yeni yetkinlik tanimlar
    @PostMapping("/api/admin/skills")
    public Skill createSkill(@RequestBody CreateSkillRequest request) {
        User admin = currentUserResolver.getCurrentUser();
        return skillService.createSkill(request.getName(), admin);
    }

    // Admin: yetkinlik siler
    @DeleteMapping("/api/admin/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        User admin = currentUserResolver.getCurrentUser();
        skillService.deleteSkill(id, admin);
        return ResponseEntity.noContent().build();
    }

    // Kullanici kendi yetkinliklerini gorur
    @GetMapping("/api/users/me/skills")
    public List<Skill> getMySkills() {
        User currentUser = currentUserResolver.getCurrentUser();
        return skillService.getUserSkills(currentUser);
    }

    // Kullanici kendi profiline bir yetkinlik ekler
    @PostMapping("/api/users/me/skills/{skillId}")
    public ResponseEntity<Void> addMySkill(@PathVariable Long skillId) {
        User currentUser = currentUserResolver.getCurrentUser();
        skillService.addSkillToUser(skillId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // Kullanici kendi profilinden bir yetkinligi cikarir
    @DeleteMapping("/api/users/me/skills/{skillId}")
    public ResponseEntity<Void> removeMySkill(@PathVariable Long skillId) {
        User currentUser = currentUserResolver.getCurrentUser();
        skillService.removeSkillFromUser(skillId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Getter
    @Setter
    public static class CreateSkillRequest {
        private String name;
    }
}
