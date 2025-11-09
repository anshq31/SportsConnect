package com.ansh.authconnectionsexample.connectionpractice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private String senderUsername;
    private String content;
    private LocalDateTime timeStamp;
}
