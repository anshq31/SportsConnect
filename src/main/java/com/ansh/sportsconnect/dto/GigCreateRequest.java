package com.ansh.sportsconnect.dto;

import jakarta.validation.constraints.*;
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

    @NotNull
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0",  message = "Latitude must be <= 90")
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0",  message = "Longitude must be <= 180")
    private Double longitude;
}
