package com.zero9platform.domain.activity_feed.event;

import com.zero9platform.common.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FeedEventListener {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 메인 실행 후 실행해라
    public void handleFeedEvent(FeedCreateEvent event) {

        // RabbitConfig에 정의한 Exchange와 RoutingKey를 사용해 JSON으로 전송
        rabbitTemplate.convertAndSend(
                RabbitConfig.FEED_EXCHANGE,
                RabbitConfig.FEED_ROUTING_KEY,
                event
        );
    }
}