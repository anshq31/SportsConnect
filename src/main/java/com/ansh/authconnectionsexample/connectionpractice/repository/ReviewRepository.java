package com.ansh.authconnectionsexample.connectionpractice.repository;

import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Review;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByParticipant(User participant);

    @Query("SELECT AVG(r.rating) FROM REVIEW r WHERE r.participant.id = :participantId")
    Double calculateAverageRating(Long participantId);
}
