package com.techdevweb.techdevbackend.Skill.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Skill.Entity.Skill;

import java.util.List;

public interface SkillService {

    // Herkese acik - yetkinlik katalogunu listeler
    List<Skill> getAllSkills();

    // Admin: katalogda yeni bir yetkinlik tanimlar
    Skill createSkill(String name, User admin);

    // Admin: katalogdan yetkinlik siler
    void deleteSkill(Long id, User admin);

    // Kullanici kendi profiline bir yetkinlik ekler
    void addSkillToUser(Long skillId, User user);

    // Kullanici kendi profilinden bir yetkinligi cikarir
    void removeSkillFromUser(Long skillId, User user);

    // Kullanicinin sahip oldugu yetkinlikler (Profile endpoint'i icin)
    List<Skill> getUserSkills(User user);
}
