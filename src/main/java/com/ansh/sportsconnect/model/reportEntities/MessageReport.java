package com.ansh.sportsconnect.model.reportEntities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"message_id", "reporter_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
