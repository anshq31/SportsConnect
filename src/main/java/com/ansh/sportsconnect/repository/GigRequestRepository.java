package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.enums.RequestStatus;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Gig;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.GigRequest;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface GigRequestRepository extends JpaRepository<GigRequest,Long> {
    Page<GigRequest> findByGigAndStatus(Gig gig, RequestStatus status, Pageable pageable);
    @Transactional
    void deleteByGig(Gig gig);

    Optional<GigRequest> findByGigAndRequester(Gig gig, User user);

    boolean existsByGigAndRequesterAndStatus(
            Gig gig,
            User requester,
            RequestStatus status
    );
}
