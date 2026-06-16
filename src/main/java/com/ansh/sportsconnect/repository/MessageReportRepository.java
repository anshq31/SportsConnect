package com.ansh.sportsconnect.repository;

import com.ansh.sportsconnect.model.reportEntities.MessageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageReportRepository extends JpaRepository<MessageReport, Long> {

    boolean existsByMessageIdAndReporterId(Long messageId, Long reporterId);

    long countByMessageId(Long messageId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM MessageReport mr WHERE mr.reporterId = :reporterId")
    void deleteAllByReporterId(@Param("reporterId") Long reporterId);
}
