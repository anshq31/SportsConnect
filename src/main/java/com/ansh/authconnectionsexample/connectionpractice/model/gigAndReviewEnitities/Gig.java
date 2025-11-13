package com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities;

import com.ansh.authconnectionsexample.connectionpractice.model.enums.GigStatus;
import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "gigs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gig_master_id",  nullable = false)
    private User gigMaster;

    @Column(nullable = false)
    private String sport;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private Integer playersNeeded;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GigStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "gig_participants",
            joinColumns = @JoinColumn(name = "gig_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> acceptedParticipants = new HashSet<>();

    @PrePersist
    protected void OnCreate(){
        if (this.status == null) {
            this.status = GigStatus.ACTIVE;
        }
    }
}
