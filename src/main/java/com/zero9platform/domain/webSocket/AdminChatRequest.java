package com.zero9platform.domain.webSocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminChatRequest {

    private Long userId;
    private String message;
}
