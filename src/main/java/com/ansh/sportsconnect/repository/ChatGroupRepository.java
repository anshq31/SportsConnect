package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.chatEntities.ChatGroup;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Gig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup,Long> {
    Optional<ChatGroup> findByGig(Gig gig);
    Optional<ChatGroup> findByGigId(Long GigId);
    void deleteByGig(Gig gig);
}
