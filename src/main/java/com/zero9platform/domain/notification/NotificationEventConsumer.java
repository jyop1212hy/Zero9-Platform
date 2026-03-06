package com.zero9platform.domain.notification;

import org.springframework.stereotype.Component;
import com.zero9platform.common.config.RabbitConfig;
import com.zero9platform.domain.gpp_follow.repository.FollowRepository;
import com.zero9platform.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitConfig.PRODUCT_STATUS_QUEUE)
    public void handleGroupPurchasePostStatusChanged(GroupPurchasePostStatusChangedEvent event) {

        log.info("[알림 소비] 공동구매 상태 변경 이벤트 수신 gppId={}, oldStatus={}, newStatus={}",
                event.getGroupPurchasePostId(), event.getOldStatus(), event.getNewStatus());

        // 1. 해당 공동구매 게시물을 팔로우한 유저 목록 조회
        List<Long> userIds = followRepository.findUserIdsByGroupPurchasePostId(event.getGroupPurchasePostId());

        if (userIds == null || userIds.isEmpty()) {
            log.info("[알림 소비] 팔로우 유저 없음 gppId={}", event.getGroupPurchasePostId());
            return;
        }

        // 2. 알림 문구 생성
        String title = "공동구매 상태 변경";
        String content = String.format(
                "[%s] 상태가 %s → %s 로 변경되었습니다.",
                event.getGroupPurchasePostTitle(),
                event.getOldStatus(),
                event.getNewStatus()
        );

        // 3. 유저별 알림 엔티티 생성
        List<Notification> notifications = userIds.stream()
                .map(userId -> new Notification(
                        userId,
                        event.getGroupPurchasePostId(),
                        title,
                        content
                ))
                .toList();

        // 4. DB 저장
        notificationRepository.saveAll(notifications);

        log.info("[알림 소비] 알림 저장 완료 count={}", notifications.size());
    }
}