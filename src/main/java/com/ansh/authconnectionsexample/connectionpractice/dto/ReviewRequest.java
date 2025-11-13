package com.ansh.authconnectionsexample.connectionpractice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    @NotNull
    private Long gigId;

    @NotNull
    private Long participantId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;

}
