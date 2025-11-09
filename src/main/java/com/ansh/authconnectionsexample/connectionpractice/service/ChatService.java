package com.ansh.authconnectionsexample.connectionpractice.service;

import com.ansh.authconnectionsexample.connectionpractice.dto.ChatMessageDto;
import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatGroup;
import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatMessage;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import com.ansh.authconnectionsexample.connectionpractice.repository.ChatGroupRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.ChatMessageRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    public void addMemberToGigChat(Gig gig, User newMember){
        ChatGroup group = chatGroupRepository.findByGig(gig)
                .orElseGet(()->{
                    ChatGroup newGroup = ChatGroup.builder()
                            .gig(gig)
                            .members(new HashSet<>())
                            .build();

                    newGroup.getMembers().add(gig.getGigMaster());
                    return newGroup;
                });
        group.getMembers().add(newMember);
        chatGroupRepository.save(group);
    }

    public ChatMessage saveMessage(Long groupId, String senderUsername, ChatMessageDto chatMessageDto){
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(()->new UsernameNotFoundException("Sender not found"));
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(()-> new RuntimeException("Chat group not found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .group(group)
                .sender(sender)
                .content(chatMessageDto.getContent())
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getChatHistory(Long groupId){
      ChatGroup group = chatGroupRepository.findById(groupId)
              .orElseThrow(()->new RuntimeException("Chat group not found"));

      List<ChatMessage> messages = chatMessageRepository.findByGroup(group);
      return messages.stream()
              .map(this::mapToChatMessageDto)
              .collect(Collectors.toList());
    }

    public void deleteChatGroupForGig(Gig gig){
        chatGroupRepository.findByGig(gig).ifPresent(group -> {
            chatMessageRepository.deleteByGroup(group);
            chatGroupRepository.delete(group);
        });
    }

    private ChatMessageDto mapToChatMessageDto(ChatMessage message){
        return ChatMessageDto.builder()
                .senderUsername(message.getSender().getUsername())
                .content(message.getContent())
                .timeStamp(message.getTimeStamp())
                .build();
    }
}
