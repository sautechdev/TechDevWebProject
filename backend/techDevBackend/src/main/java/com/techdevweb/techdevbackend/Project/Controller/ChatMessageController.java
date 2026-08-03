package com.techdevweb.techdevbackend.Project.Controller;

import com.techdevweb.techdevbackend.Entity.User;
import com.techdevweb.techdevbackend.Project.DTO.ChatMessageRequest;
import com.techdevweb.techdevbackend.Project.Entity.ChatMessage;
import com.techdevweb.techdevbackend.Project.Service.ChatMessageService;
import com.techdevweb.techdevbackend.Security.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/chat/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final CurrentUserResolver currentUserResolver;

    // Polling: frontend birkac saniyede bir ?after={lastId} ile cagirir
    @GetMapping
    public List<ChatMessage> getMessages(@PathVariable Long projectId,
                                          @RequestParam(required = false) Long after) {
        User currentUser = currentUserResolver.getCurrentUser();
        return chatMessageService.getMessages(projectId, after, currentUser);
    }

    @PostMapping
    public ChatMessage send(@PathVariable Long projectId,
                             @RequestBody ChatMessageRequest request) {
        User sender = currentUserResolver.getCurrentUser();
        return chatMessageService.sendMessage(projectId, sender, request.getContent());
    }
}
