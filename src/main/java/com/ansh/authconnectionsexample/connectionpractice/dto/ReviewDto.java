package com.ansh.authconnectionsexample.connectionpractice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long gigId;
    private String reviewerUsername;
    private Integer rating;
    private String comment;
}
