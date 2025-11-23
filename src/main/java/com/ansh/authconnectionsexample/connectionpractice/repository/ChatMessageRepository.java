package com.ansh.authconnectionsexample.connectionpractice.repository;

import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatGroup;
import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    Page<ChatMessage> findByGroup(ChatGroup group, Pageable pageable);

    @Transactional
    void deleteByGroup(ChatGroup group);
}
