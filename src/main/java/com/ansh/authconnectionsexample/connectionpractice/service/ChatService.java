package com.ansh.authconnectionsexample.connectionpractice.service;

import com.ansh.authconnectionsexample.connectionpractice.dto.ChatMessageDto;
import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatGroup;
import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatMessage;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import com.ansh.authconnectionsexample.connectionpractice.repository.ChatGroupRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.ChatMessageRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.GigRepository;
import com.ansh.authconnectionsexample.connectionpractice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GigRepository gigRepository;

    @Transactional
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

    @Transactional
    public ChatMessage saveMessage(Long groupId, String senderUsername, ChatMessageDto chatMessageDto){
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(()->new UsernameNotFoundException("Sender not found"));
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseGet(()->{
                    Gig gig = gigRepository.findById(groupId)
                            .orElseThrow(() -> new RuntimeException("Chat group and associated Gig not found for ID: " + groupId));

                    ChatGroup newGroup = ChatGroup.builder()
                            .id(groupId)
                            .gig(gig)
                            .members(new HashSet<>())
                            .build();
                    newGroup.getMembers().add(gig.getGigMaster());
                    return chatGroupRepository.save(newGroup);
                });

        ChatMessage chatMessage = ChatMessage.builder()
                .group(group)
                .sender(sender)
                .content(chatMessageDto.getContent())
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getChatHistory(Long groupId, Pageable pageable){
      Optional<ChatGroup> group = chatGroupRepository.findById(groupId);

        if (group.isEmpty()) {
            return Page.empty(pageable);
        }

      Page<ChatMessage> messages = chatMessageRepository.findByGroup(group.get(),pageable);
      return messages.map(this::mapToChatMessageDto);
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
