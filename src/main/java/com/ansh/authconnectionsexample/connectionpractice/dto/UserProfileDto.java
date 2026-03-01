package com.ansh.authconnectionsexample.connectionpractice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private Long id;
    private String username;
    private String experience;
    private BigDecimal overallRating;
    private Set<String> skill;
    private List<ReviewDto> reviewsReceived;
}
