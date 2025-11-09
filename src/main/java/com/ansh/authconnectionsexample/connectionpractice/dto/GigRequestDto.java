package com.ansh.authconnectionsexample.connectionpractice.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GigRequestDto {
    private Long requestId;
    private Long requesterId;
    private String requesterUsername;
}
