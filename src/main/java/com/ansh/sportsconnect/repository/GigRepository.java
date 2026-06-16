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

    @Query(value =
        "SELECT g.id FROM gigs g " +
        "WHERE g.status = 'ACTIVE' " +
        "AND g.gig_master_id != :userId " +
        "AND g.id NOT IN (SELECT gp.gig_id FROM gig_participants gp WHERE gp.user_id = :userId) " +
        "AND g.latitude IS NOT NULL AND g.longitude IS NOT NULL " +
        "AND (:sport IS NULL OR LOWER(g.sport) LIKE :sport) " +
        "AND g.gig_master_id NOT IN (" +
        "  SELECT ub1.blocked_id FROM user_blocks ub1 WHERE ub1.blocker_id = :userId " +
        "  UNION " +
        "  SELECT ub2.blocker_id FROM user_blocks ub2 WHERE ub2.blocked_id = :userId" +
        ") " +
        "AND (6371 * acos(GREATEST(-1.0, LEAST(1.0, " +
        "  cos(radians(:lat)) * cos(radians(g.latitude)) " +
        "  * cos(radians(g.longitude) - radians(:lng)) " +
        "  + sin(radians(:lat)) * sin(radians(g.latitude)) " +
        ")))) <= :radiusKm",
        nativeQuery = true)
    List<Long> findGigIdsWithinRadius(
        @Param("userId") Long userId,
        @Param("sport")  String sport,
        @Param("lat")    Double lat,
        @Param("lng")    Double lng,
        @Param("radiusKm") Double radiusKm
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM gig_participants WHERE user_id = :targetUserId " +
                   "AND gig_id IN (SELECT id FROM gigs WHERE gig_master_id = :ownerId)",
           nativeQuery = true)
    void removeUserFromOwnerGigs(@Param("ownerId") Long ownerId, @Param("targetUserId") Long targetUserId);

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
