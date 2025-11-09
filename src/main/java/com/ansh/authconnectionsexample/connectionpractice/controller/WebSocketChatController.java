package com.ansh.authconnectionsexample.connectionpractice.controller;

import com.ansh.authconnectionsexample.connectionpractice.dto.ChatMessageDto;
import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatMessage;
import com.ansh.authconnectionsexample.connectionpractice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/{groupId}/send")
    public void sendMessage(
            @DestinationVariable Long groupId,
            @Payload ChatMessageDto chatMessageDto,
            Principal principal
            ){
        String username = principal.getName();

        ChatMessage savedMessage = chatService.saveMessage(groupId,username,chatMessageDto);

        String destination = "/topic/chat/" + groupId;

        ChatMessageDto broadcastDto = ChatMessageDto.builder()
                .senderUsername(savedMessage.getSender().getUsername())
                .content(savedMessage.getContent())
                .timeStamp(savedMessage.getTimeStamp())
                .build();

        messagingTemplate.convertAndSend(destination,broadcastDto);
    }
}
