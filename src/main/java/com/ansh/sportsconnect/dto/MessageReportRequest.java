package com.ansh.sportsconnect.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReportRequest {
    private String reason;
}
