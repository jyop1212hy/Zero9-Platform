package com.zero9platform.domain.activity_feed.consumer;

import com.zero9platform.common.config.RabbitConfig;
import com.zero9platform.domain.activity_feed.event.FeedCreateEvent;
import com.zero9platform.domain.activity_feed.service.ActivityFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityFeedConsumer {

    private final ActivityFeedService activityFeedService;

    /**
     * @RabbitListener: 해당 큐를 실시간 모니터링함
     * 메시지가 들어오면 자동으로 아래 메서드가 실행됨
     */
    @RabbitListener(queues = RabbitConfig.FEED_QUEUE)
    public void consumeFeedEvent(FeedCreateEvent event) {

        log.info("[RabbitMQ Consumer] 메시지 수신 - 유저: {}, 타입: {}", event.getUserId(), event.getType());

        try {
            // 실제 피드 생성 로직(DB 저장 및 Redis 업데이트) 호출
            activityFeedService.feedCreate(
                    event.getType(),
                    event.getTargetId(),
                    event.getTitle(),
                    event.getUserId()
            );
        } catch (Exception e) {
            // 실패 시 로그를 남기고 메시지는 큐의 설정에 따라 재시도
            log.error("[RabbitMQ Consumer] 피드 처리 중 에러 발생: {}", e.getMessage());

            throw e;
        }
    }
}