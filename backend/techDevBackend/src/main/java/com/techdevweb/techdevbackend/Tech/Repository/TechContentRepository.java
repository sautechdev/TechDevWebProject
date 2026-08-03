package com.techdevweb.techdevbackend.Tech.Repository;

import com.techdevweb.techdevbackend.Tech.Entity.TechContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechContentRepository extends JpaRepository<TechContent, Long> {
    Optional<TechContent> findByTechStackId(Long stackId);
    boolean existsByTechStackId(Long stackId);
}
