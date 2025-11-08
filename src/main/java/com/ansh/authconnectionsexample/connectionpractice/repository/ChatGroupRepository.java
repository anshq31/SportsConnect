package com.ansh.authconnectionsexample.connectionpractice.repository;

import com.ansh.authconnectionsexample.connectionpractice.model.chatEntities.ChatGroup;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup,Long> {
    Optional<ChatGroup> findByGig(Gig gig);

    void deleteGig(Gig gig);
}
