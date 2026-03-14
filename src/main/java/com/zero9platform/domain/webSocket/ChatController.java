package com.zero9platform.domain.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(ChatMessage message) {

        messagingTemplate.convertAndSend(
                "/queue/chat/" + message.getReceiverId(),
                message
        );
    }
}
