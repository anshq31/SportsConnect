package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.chatEntities.ChatGroup;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Gig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup,Long> {
    Optional<ChatGroup> findByGig(Gig gig);
    Optional<ChatGroup> findByGigId(Long gigId);
    void deleteByGig(Gig gig);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM chat_group_members WHERE chat_group_id IN " +
                   "(SELECT id FROM chat_groups WHERE gig_id IN :gigIds)", nativeQuery = true)
    void deleteMembersForGigIds(@Param("gigIds") List<Long> gigIds);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM chat_groups WHERE gig_id IN :gigIds", nativeQuery = true)
    void deleteAllByGigIdIn(@Param("gigIds") List<Long> gigIds);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM chat_group_members WHERE user_id = :userId", nativeQuery = true)
    void removeUserFromAllGroups(@Param("userId") Long userId);
}
