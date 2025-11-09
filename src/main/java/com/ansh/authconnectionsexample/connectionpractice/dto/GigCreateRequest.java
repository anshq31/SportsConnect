package com.ansh.authconnectionsexample.connectionpractice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GigCreateRequest {
    @NotBlank
    private String sport;

    @NotBlank
    private String location;

    @NotNull
    @Future(message = "Date and time must be in the future")
    private LocalDateTime dateTime;

    @NotNull
    @Min(value = 1,message = "Must need at least 1 player")
    private Integer playersNeeded;
}
