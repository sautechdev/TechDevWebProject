package com.techdevweb.techdevbackend.Skill.Repository;

import com.techdevweb.techdevbackend.Skill.Entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByName(String name);
    Optional<Skill> findByName(String name);
}
