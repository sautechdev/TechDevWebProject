package com.techdevweb.techdevbackend.Tech.Repository;

import com.techdevweb.techdevbackend.Tech.Entity.TechField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TechFieldRepository extends JpaRepository<TechField, Long> {
    Optional<TechField> findByName(String name);
    boolean existsByName(String name);
}
