package com.zero9platform.domain.notification.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("/notifications")
public class NotificationSseController {

    private final SseEmitterManager emitterManager;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam Long userId) {

        return emitterManager.connect(userId);

    }
}