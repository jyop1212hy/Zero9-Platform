package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.notification.GroupPurchasePostStatusChangedEvent;
import com.zero9platform.domain.notification.NotificationEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupPurchasePostStatusScheduler {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final NotificationEventProducer notificationEventProducer;

    /**
     * 매일 00시에 모집 상태 자동 변경
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // cron 내부적으로 이전 실행이 끝난 후 다음 스케줄을 처리
    public void updateGppProgressStatus() {

        log.info("GPP 모집상태 변경 시작, 실행 스레드명 : {}", Thread.currentThread().getName());

        LocalDateTime now = LocalDateTime.now();

        // 1. 상태 변경 대상 미리 조회
        List<GroupPurchasePost> readyPosts =
                groupPurchasePostRepository.findAllByGppProgressStatusAndStartDateLessThanEqualAndDeletedAtIsNull(
                        "READY", now
                );

        List<GroupPurchasePost> doingPosts =
                groupPurchasePostRepository.findAllByGppProgressStatusAndEndDateLessThanEqualAndDeletedAtIsNull(
                        "DOING", now
                );

        // 2. 벌크 업데이트 실행
        int readyToDoing = groupPurchasePostRepository.updateReadyToDoing(now);
        int doingToEnd = groupPurchasePostRepository.updateDoingToEnd(now);

        // 3. READY -> DOING 이벤트 발행
        for (GroupPurchasePost post : readyPosts) {
            notificationEventProducer.publishGroupPurchasePostStatusChanged(
                    new GroupPurchasePostStatusChangedEvent(
                            post.getId(),
                            post.getProductName(),
                            "READY",
                            "DOING")
            );
        }

        // 4. DOING -> END 이벤트 발행
        for (GroupPurchasePost post : doingPosts) {
            notificationEventProducer.publishGroupPurchasePostStatusChanged(
                    new GroupPurchasePostStatusChangedEvent(
                            post.getId(),
                            post.getProductName(),
                            "DOING",
                            "END"
                    )
            );
        }
        log.info("GPP 모집상태 변경 - READY->DOING: {}, DOING->END: {}", readyToDoing, doingToEnd);
        log.info("GPP 알림 이벤트 발행 - READY->DOING: {}, DOING->END: {}", readyPosts.size(), doingPosts.size());
    }
}