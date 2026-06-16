package com.ansh.sportsconnect.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReportDto {
    private Long id;
    private Long messageId;
    private Long reporterId;
    private String reason;
    private LocalDateTime createdAt;
}
