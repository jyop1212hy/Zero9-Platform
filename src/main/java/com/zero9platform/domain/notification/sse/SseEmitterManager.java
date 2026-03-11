package com.zero9platform.domain.notification.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterManager {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(60L * 60 * 1000); // 1시간 타임아웃 설정
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // [핵심 추가] 연결 직후 더미 데이터를 보내서 503 에러 및 연결 끊김 방지
        try {
            emitter.send(SseEmitter.event()
                    .name("connect") // 프론트의 addEventListener("connect")와 매칭
                    .data("connected!"));
        } catch (IOException e) {
            emitters.remove(userId);
        }
        return emitter;
    }

    public void send(Long userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification") // 프론트의 addEventListener("notification")와 매칭
                        .data(data));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}
