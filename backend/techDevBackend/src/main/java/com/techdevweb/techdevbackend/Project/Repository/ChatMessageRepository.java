package com.techdevweb.techdevbackend.Project.Repository;

import com.techdevweb.techdevbackend.Project.Entity.ChatMessage;
import com.techdevweb.techdevbackend.Project.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Chat gecmisini kronolojik sirayla getirir
    List<ChatMessage> findByProjectOrderBySentAtAsc(Project project);

    // Polling icin: GET /chat/messages?after={lastId}
    // Sadece verilen id'den sonraki yeni mesajlari getirir
    List<ChatMessage> findByProject_IdAndIdGreaterThanOrderByIdAsc(Long projectId, Long afterId);
}
