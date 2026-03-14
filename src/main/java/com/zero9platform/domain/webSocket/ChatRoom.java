package com.zero9platform.domain.webSocket;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "chat_rooms",
        indexes = {
                @Index(name = "idx_room_user", columnList = "user_id"),
                @Index(name = "idx_room_last_message", columnList = "last_message_time")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "last_message", nullable = false)
    private String lastMessage;

    @Column(name = "last_message_time", nullable = false)
    private LocalDateTime lastMessageTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ChatRoom(Long userId, Long adminId) {
        this.userId = userId;
        this.adminId = adminId;
        this.lastMessage = "";
        this.lastMessageTime = LocalDateTime.now();
    }

    public void updateLastMessage(String message) {
        this.lastMessage = message;
        this.lastMessageTime = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}