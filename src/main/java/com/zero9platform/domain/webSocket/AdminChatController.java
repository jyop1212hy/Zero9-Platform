package com.zero9platform.domain.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class AdminChatController {

    private final ChatMessageService chatMessageService;

    @PostMapping("/admin/chat/send")
    public void sendToUser(@RequestBody AdminChatRequest request) {

        chatMessageService.sendToUser(
                request.getUserId(),
                request.getMessage()
        );
    }
}
