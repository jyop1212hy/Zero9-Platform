package com.zero9platform.domain.webSocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomPreviewResponse {

    private Long roomId;
    private Long userId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
