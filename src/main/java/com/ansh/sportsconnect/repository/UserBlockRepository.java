package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.blockEntities.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    @Query("SELECT ub.blockedId FROM UserBlock ub WHERE ub.blockerId = :blockerId")
    List<Long> findBlockedIdsByBlockerId(@Param("blockerId") Long blockerId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM UserBlock ub WHERE ub.blockerId = :blockerId AND ub.blockedId = :blockedId")
    void deleteByBlockerIdAndBlockedId(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM UserBlock ub WHERE ub.blockerId = :userId OR ub.blockedId = :userId")
    void deleteAllInvolvingUser(@Param("userId") Long userId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM user_blocks WHERE " +
                   "(blocker_id = :userAId AND blocked_id = :userBId) OR " +
                   "(blocker_id = :userBId AND blocked_id = :userAId))",
           nativeQuery = true)
    boolean existsBlockBetween(@Param("userAId") Long userAId, @Param("userBId") Long userBId);

    @Query(value = "SELECT blocked_id FROM user_blocks WHERE blocker_id = :userId " +
                   "UNION " +
                   "SELECT blocker_id FROM user_blocks WHERE blocked_id = :userId",
           nativeQuery = true)
    List<Long> findAllBlockedUserIds(@Param("userId") Long userId);
}
