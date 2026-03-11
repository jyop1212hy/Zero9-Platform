package com.zero9platform.domain.notification;

import com.zero9platform.common.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishGroupPurchasePostStatusChanged(GroupPurchasePostStatusChangedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.NOTIFICATION_EXCHANGE,
                RabbitConfig.PRODUCT_STATUS_ROUTING_KEY,
                event
        );
    }
}
