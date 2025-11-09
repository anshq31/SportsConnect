package com.ansh.authconnectionsexample.connectionpractice.controller;

import com.ansh.authconnectionsexample.connectionpractice.dto.ChatMessageDto;
import com.ansh.authconnectionsexample.connectionpractice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("{groupId}/history")
    public ResponseEntity<List<ChatMessageDto>> getHistory(@PathVariable Long groupId){
        List<ChatMessageDto> history = chatService.getChatHistory(groupId);
        return ResponseEntity.ok(history);
    }
}
