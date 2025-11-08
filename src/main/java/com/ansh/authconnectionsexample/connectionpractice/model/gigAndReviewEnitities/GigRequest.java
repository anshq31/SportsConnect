package com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities;

import com.ansh.authconnectionsexample.connectionpractice.model.enums.RequestStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "gig_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GigRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gig_id",nullable = false)
    private Gig gig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @PrePersist
    protected void OnCreate(){
        if (this.status == null) {
            this.status = RequestStatus.PENDING;
        }
    }
}
