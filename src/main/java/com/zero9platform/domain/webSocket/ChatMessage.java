package com.zero9platform.domain.webSocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ChatMessage {

    private final String senderId;
    private final Long receiverId;
    private final String message;
}
