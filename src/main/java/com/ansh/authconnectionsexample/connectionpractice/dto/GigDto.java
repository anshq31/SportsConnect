package com.ansh.authconnectionsexample.connectionpractice.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GigDto {
    private Long id;
    private String sport;
    private String location;
    private LocalDateTime dateTime;
    private Integer playersNeeded;
    private String status;
    private String gigMasterUsername;
    private Set<String> acceptedParticipants;
}
