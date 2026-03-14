package com.zero9platform.common.config;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-zero9") // 클라이언트 접속 엔드포인트
                .setAllowedOriginPatterns("*")
                .withSockJS(); // 구형 브라우저 대응
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // /topic: 일대다 방송 (알림, 게이지)
        // /queue: 일대일 전송 (문의 채팅)
        registry.enableSimpleBroker("/topic", "/queue");

        registry.setApplicationDestinationPrefixes("/app");
    }
}
