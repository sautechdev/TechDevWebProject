package com.techdevweb.techdevbackend.Project.Service;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {

    // afterId null ise tum gecmisi, dolu ise sadece o id'den sonrasini getirir (polling icin)
    List<ChatMessage> getMessages(Long projectId, Long afterId, User currentUser);

    // Herhangi bir uye (OWNER dahil) mesaj gonderebilir
    ChatMessage sendMessage(Long projectId, User sender, String content);
}
