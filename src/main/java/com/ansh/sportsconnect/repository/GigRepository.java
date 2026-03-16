package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.enums.GigStatus;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Gig;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GigRepository extends JpaRepository<Gig,Long>,JpaSpecificationExecutor<Gig> {

//    Page<Gig> findByStatus(GigStatus status, Pageable pageable);
    Optional<Gig> findByGigMasterAndStatusIn(User gigMaster, List<GigStatus> statuses);
    List<Gig>  findByStatusInAndDateTimeBefore(List<GigStatus> statuses, LocalDateTime dateTime);

    List<Gig> findByStatusAndCompletedAtBefore(GigStatus status, LocalDateTime cutoff);
}
