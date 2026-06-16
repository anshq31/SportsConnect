package com.ansh.sportsconnect.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBlockDto {
    private Long id;
    private Long blockerId;
    private Long blockedId;
    private LocalDateTime createdAt;
}
