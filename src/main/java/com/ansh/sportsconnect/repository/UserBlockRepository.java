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
}
