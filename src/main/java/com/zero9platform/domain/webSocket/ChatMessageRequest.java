package com.zero9platform.domain.webSocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageRequest {

    private Long userId;
    private final String message;
}
