package com.ansh.sportsconnect.controller;

import com.ansh.sportsconnect.dto.ChatMessageDto;
import com.ansh.sportsconnect.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/{groupId}/history")
    public ResponseEntity<Page<ChatMessageDto>> getHistory(@PathVariable Long groupId, @PageableDefault(size = 20,sort = "timeStamp",direction = Sort.Direction.DESC)Pageable pageable){
        Page<ChatMessageDto> history = chatService.getChatHistory(groupId,pageable);
        return ResponseEntity.ok(history);
    }
}