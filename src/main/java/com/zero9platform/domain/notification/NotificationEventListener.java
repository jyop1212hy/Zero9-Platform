package com.zero9platform.domain.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationEventProducer notificationEventProducer;

    //공동구매 게시물 벌크스케줄링후 트랜잭션 commit 이후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(GroupPurchasePostStatusChangedEvent event) {

//        notificationEventProducer.publishGroupPurchasePostStatusChanged(event);
        notificationEventProducer.publish(event);

    }
}