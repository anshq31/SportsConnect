package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.enums.GigStatus;
import com.ansh.sportsconnect.model.gigAndReviewEnitities.Gig;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GigRepository extends JpaRepository<Gig,Long>,JpaSpecificationExecutor<Gig> {

//    Page<Gig> findByStatus(GigStatus status, Pageable pageable);
    Optional<Gig> findByGigMasterAndStatusIn(User gigMaster, List<GigStatus> statuses);
    List<Gig>  findByStatusInAndDateTimeBefore(List<GigStatus> statuses, LocalDateTime dateTime);

    List<Gig> findByStatusInAndCompletedAtBefore(List<GigStatus> statuses, LocalDateTime cutoff);

    @Query("SELECT g.id FROM Gig g WHERE g.gigMaster.id = :userId")
    List<Long> findIdsByGigMasterId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM gig_participants WHERE gig_id IN :gigIds", nativeQuery = true)
    void clearParticipantsByGigIds(@Param("gigIds") List<Long> gigIds);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM gig_participants WHERE user_id = :userId", nativeQuery = true)
    void removeUserFromAllParticipants(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Gig g WHERE g.id IN :gigIds")
    void deleteAllByIdIn(@Param("gigIds") List<Long> gigIds);
}
