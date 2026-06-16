package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.chatEntities.ChatGroup;
import com.ansh.sportsconnect.model.chatEntities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    Page<ChatMessage> findByGroupAndHiddenFalse(ChatGroup group, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE m.group = :group AND m.hidden = false AND m.sender.id NOT IN :blockedIds")
    Page<ChatMessage> findByGroupAndHiddenFalseAndSenderNotIn(
            @Param("group") ChatGroup group,
            @Param("blockedIds") List<Long> blockedIds,
            Pageable pageable);

    @Transactional
    void deleteByGroup(ChatGroup group);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM ChatMessage m WHERE m.sender.id = :userId")
    void deleteAllBySenderId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM chat_messages WHERE chat_group_id IN " +
                   "(SELECT id FROM chat_groups WHERE gig_id IN :gigIds)", nativeQuery = true)
    void deleteAllByGigIds(@Param("gigIds") List<Long> gigIds);
}
