package com.ansh.authconnectionsexample.connectionpractice.repository;

import com.ansh.authconnectionsexample.connectionpractice.model.enums.GigStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Gig;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GigRepository extends JpaRepository<Gig,Long>,JpaSpecificationExecutor<Gig> {

//    Page<Gig> findByStatus(GigStatus status, Pageable pageable);

    Optional<Gig> findByGigMasterAndStatusIn(User gigMaster, List<GigStatus> statuses);
}
