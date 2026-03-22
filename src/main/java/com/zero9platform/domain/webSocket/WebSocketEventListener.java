package com.zero9platform.domain.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisTemplate<String, String> redisTemplate;


    /**
     * 접속 시 온라인 등록
     */
    @EventListener
    public void handleConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = accessor.getFirstNativeHeader("userId");

        if (userId != null) {
            accessor.getSessionAttributes().put("userId", userId);
            redisTemplate.opsForSet().add("online_users", userId);
        }
    }

    /**
     * 접속 종료 시 오프라인 처리
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) accessor.getSessionAttributes().get("userId");

        if (userId != null) {
            redisTemplate.opsForSet().remove("online_users", userId);
        }
    }
}
