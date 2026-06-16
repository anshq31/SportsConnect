package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.gigAndReviewEnitities.Review;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByParticipant(User participant);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.participant.id = :participantId")
    Double calculateAverageRating(Long participantId);


    boolean existsByGigIdAndParticipant(Long gigId, User participant);

    Page<Review> findByParticipant(User participant,Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Review r WHERE r.reviewer.id = :userId OR r.participant.id = :userId")
    void deleteAllInvolvingUser(@Param("userId") Long userId);
}
