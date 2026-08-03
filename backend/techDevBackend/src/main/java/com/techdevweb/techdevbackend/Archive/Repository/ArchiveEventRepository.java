package com.techdevweb.techdevbackend.Archive.Repository;

import com.techdevweb.techdevbackend.Archive.Entity.ArchiveEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArchiveEventRepository extends JpaRepository<ArchiveEvent, Long> {

    // Arama + yıl filtresi + pagination hepsi birlikte
    @Query("SELECT a FROM ArchiveEvent a WHERE " +
            "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) " +
            "OR LOWER(a.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
            "AND (:year IS NULL OR YEAR(a.eventDate) = :year)")
    Page<ArchiveEvent> search(@Param("keyword") String keyword,
                              @Param("year") Integer year,
                              Pageable pageable);
}
