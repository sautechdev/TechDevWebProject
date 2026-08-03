package com.techdevweb.techdevbackend.Notification.Repository;

import com.techdevweb.techdevbackend.Notification.Entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndReadFalse(Long userId);
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
            "AND (:onlyUnread = false OR n.read = false) " +
            "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserWithFilter(@Param("userId") Long userId,
                                            @Param("onlyUnread") boolean onlyUnread,
                                            Pageable pageable);
}
