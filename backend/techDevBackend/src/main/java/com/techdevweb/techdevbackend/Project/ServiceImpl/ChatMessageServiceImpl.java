package com.techdevweb.techdevbackend.Project.ServiceImpl;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.Entity.ChatMessage;
import com.techdevweb.techdevbackend.Project.Entity.ProjectMember;
import com.techdevweb.techdevbackend.Project.Repository.ChatMessageRepository;
import com.techdevweb.techdevbackend.Project.Service.ChatMessageService;
import com.techdevweb.techdevbackend.Project.Service.ProjectAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ProjectAccessGuard accessGuard;

    @Override
    public List<ChatMessage> getMessages(Long projectId, Long afterId, User currentUser) {
        accessGuard.requireMember(projectId, currentUser);

        // afterId verilmemisse 0'dan baslat (ID'ler 1'den basladigi icin tum gecmisi getirir)
        long since = afterId != null ? afterId : 0L;
        return chatMessageRepository.findByProject_IdAndIdGreaterThanOrderByIdAsc(projectId, since);
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(Long projectId, User sender, String content) {
        ProjectMember membership = accessGuard.requireMembership(projectId, sender);

        ChatMessage message = new ChatMessage();
        message.setProject(membership.getProject());
        message.setSender(sender);
        message.setContent(content);
        return chatMessageRepository.save(message);
    }
}
