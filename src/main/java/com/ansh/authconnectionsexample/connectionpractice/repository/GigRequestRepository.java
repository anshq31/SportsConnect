package com.ansh.authconnectionsexample.connectionpractice.repository;

import com.ansh.authconnectionsexample.connectionpractice.model.enums.RequestStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.GigRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GigRequestRepository extends JpaRepository<GigRequest,Long> {
    List<GigRequest> findByGigAndStatus(Gig gig, RequestStatus status);
    @Transactional
    void deleteByGig(Gig gig);
}
