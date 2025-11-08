package com.ansh.authconnectionsexample.connectionpractice.model.chatEntities;

import com.ansh.authconnectionsexample.connectionpractice.model.userAndAuthEntities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id",nullable = false)
    private User sender;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @PrePersist
    protected void onCreate(){
        this.timeStamp = LocalDateTime.now();
    }
}
