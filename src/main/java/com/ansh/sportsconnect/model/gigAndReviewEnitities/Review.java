package com.ansh.sportsconnect.model.gigAndReviewEnitities;

import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gig_id", nullable = false)
    private Long gigId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id",nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id",nullable = false)
    private User participant;

    @Column(nullable = false)
    private Integer rating;

    @Lob
    private String comment;
}
