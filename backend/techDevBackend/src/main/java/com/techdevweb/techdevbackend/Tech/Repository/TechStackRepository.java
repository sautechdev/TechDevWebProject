package com.techdevweb.techdevbackend.Tech.Repository;

import com.techdevweb.techdevbackend.Tech.Entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findByTechFieldId(Long fieldId);
    Optional<TechStack> findByNameAndTechFieldId(String name, Long fieldId);

    @Query("SELECT s FROM TechStack s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))")
    List<TechStack> searchByName(@Param("keyword") String keyword);
}
