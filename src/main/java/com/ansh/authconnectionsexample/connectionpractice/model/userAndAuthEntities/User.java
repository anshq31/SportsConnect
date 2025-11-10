package com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities;

import com.ansh.authconnectionsexample.connectionpractice.model.gigAndReviewEnitities.Review;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Lob
    private String experience;

    @Column(precision = 3, scale = 2, columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    private BigDecimal overallRating = BigDecimal.valueOf(0.0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @OneToMany(mappedBy = "participant",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsReceived;

}
