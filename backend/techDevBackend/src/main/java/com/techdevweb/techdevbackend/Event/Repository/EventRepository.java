package com.techdevweb.techdevbackend.Event.Repository;

import com.techdevweb.techdevbackend.Event.Entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE " +
            "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Event> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.cancelled = false AND e.startDateTime > :now " +
            "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Event> findUpcoming(@Param("now") LocalDateTime now, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.cancelled = false " +
            "AND e.startDateTime <= :now AND e.endDateTime >= :now " +
            "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Event> findOngoing(@Param("now") LocalDateTime now, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.endDateTime < :now " +
            "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Event> findCompleted(@Param("now") LocalDateTime now, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.cancelled = true " +
            "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Event> findCancelled(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.cancelled = false AND e.reminderSent = false " +
            "AND e.startDateTime BETWEEN :now AND :windowEnd")
    List<Event> findEventsNeedingReminder(@Param("now") LocalDateTime now,
                                          @Param("windowEnd") LocalDateTime windowEnd);
}
