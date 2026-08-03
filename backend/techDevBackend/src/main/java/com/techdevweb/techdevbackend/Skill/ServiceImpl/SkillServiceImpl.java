package com.techdevweb.techdevbackend.Skill.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Exception.ConflictException;
import com.techdevweb.techdevbackend.Exception.ResourceNotFoundException;
import com.techdevweb.techdevbackend.Repository.UserRepository;
import com.techdevweb.techdevbackend.Security.AdminAccessGuard;
import com.techdevweb.techdevbackend.Skill.Entity.Skill;
import com.techdevweb.techdevbackend.Skill.Repository.SkillRepository;
import com.techdevweb.techdevbackend.Skill.Service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final AdminAccessGuard adminAccessGuard;

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    @Transactional
    public Skill createSkill(String name, User admin) {
        adminAccessGuard.requireAdmin();
        if (skillRepository.existsByName(name)) {
            throw new ConflictException("Bu yetkinlik zaten mevcut: " + name);
        }
        Skill skill = new Skill();
        skill.setName(name);
        return skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id, User admin) {
        adminAccessGuard.requireAdmin();
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yetkinlik bulunamadi: " + id));
        skillRepository.delete(skill);
    }

    @Override
    @Transactional
    public void addSkillToUser(Long skillId, User user) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Yetkinlik bulunamadi: " + skillId));

        // Yonetilen (managed) User nesnesini tekrar cekiyoruz ki skills koleksiyonu
        // dogru sekilde yuklenip guncellensin.
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + user.getId()));

        managedUser.getSkills().add(skill);
        userRepository.save(managedUser);
    }

    @Override
    @Transactional
    public void removeSkillFromUser(Long skillId, User user) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Yetkinlik bulunamadi: " + skillId));

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + user.getId()));

        managedUser.getSkills().remove(skill);
        userRepository.save(managedUser);
    }

    @Override
    @Transactional
    public List<Skill> getUserSkills(User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanici bulunamadi: " + user.getId()));
        return new ArrayList<>(managedUser.getSkills());
    }
}
