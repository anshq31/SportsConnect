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
    public ChatMessage saveMessage(Long gigId, String senderUsername, ChatMessageDto chatMessageDto){
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(()->new UsernameNotFoundException("Sender not found"));

        ChatGroup group = resolveChatGroup(gigId,true)
                .orElseThrow(()-> new RuntimeException("Chat group and associated Gig not found for ID: " + gigId));

        if (!group.getMembers().contains(sender)){
            throw new RuntimeException("User is not a member of this chat group");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .group(group)
                .sender(sender)
                .content(chatMessageDto.getContent())
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getChatHistory(Long gigId, Pageable pageable){

        Optional<ChatGroup> group = resolveChatGroup(gigId, false);
        if (group.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<ChatMessage> messages = chatMessageRepository.findByGroup(group.get(),pageable);
        return messages.map(this::mapToChatMessageDto);
    }

    private Optional<ChatGroup> resolveChatGroup(Long gigId, boolean createIfMissing){
        Optional<ChatGroup> existingGroup = chatGroupRepository.findById(gigId)
                .or(()-> chatGroupRepository.findByGigId(gigId));

        if (existingGroup.isPresent() || !createIfMissing){
            return existingGroup;
        }

        return gigRepository.findById(gigId)
                .map(gig -> {
                    ChatGroup newGroup = ChatGroup.builder()
                            .gig(gig)
                            .members(new HashSet<>())
                            .build();
                    newGroup.getMembers().add(gig.getGigMaster());
                    return chatGroupRepository.save(newGroup);
                });
    }

    public void deleteChatGroupForGig(Gig gig){
        chatGroupRepository.findByGig(gig).ifPresent(group -> {
            chatMessageRepository.deleteByGroup(group);
            chatGroupRepository.delete(group);
        });
    }

    private ChatMessageDto mapToChatMessageDto(ChatMessage message){
        return ChatMessageDto.builder()
                .id(message.getId().toString())
                .senderUsername(message.getSender().getUsername())
                .content(message.getContent())
                .timeStamp(message.getTimeStamp())
                .build();
    }
}
