package com.zero9platform.domain.product_post.service;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.common.enums.ProgressStatus;
import com.zero9platform.domain.activity_feed.event.FeedCreateEvent;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductPostScheduler {

    private final ProductPostRepository productPostRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateProductPostProgress() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        // 상품판매 게시물 "READY" -> "DOING"
        int doingCount = productPostRepository.updateToDoing(now);

        // 오픈
        if (doingCount > 0) {

            productPostRepository.findFirstByProgressStatusOrderByUpdatedAtDesc(ProgressStatus.DOING.name())
                    .ifPresent(post -> {

                        // 수신자(userId)가 null이면 전체 공지
                        eventPublisher.publishEvent(new FeedCreateEvent(FeedType.OPEN, post.getId(), post.getTitle(), null));
                    });
        }

        // 상품판매 게시물 "DOING" -> "END"
        int endCount = productPostRepository.updateToEnd(now);

        // 종료
        if (endCount > 0) {

            productPostRepository.findFirstByProgressStatusOrderByUpdatedAtDesc(ProgressStatus.END.name())
                    .ifPresent(post -> {

                        eventPublisher.publishEvent(new FeedCreateEvent(FeedType.SOLD_OUT, post.getId(), post.getTitle(), null));
                    });
        }

        // 오픈 예고
        List<ProductPost> upcomingPosts = productPostRepository.findUpcomingPosts(now, tomorrow);

        if (!upcomingPosts.isEmpty()) {

            ProductPost post = upcomingPosts.get(0);

            eventPublisher.publishEvent(new FeedCreateEvent(FeedType.SOON, post.getId(), post.getTitle(), null));
        }

        // 마감 예고
        List<ProductPost> deadlinePosts = productPostRepository.findDeadlinePosts(now, tomorrow);

        if (!deadlinePosts.isEmpty()) {

            ProductPost representative = deadlinePosts.get(0);

            eventPublisher.publishEvent(new FeedCreateEvent(FeedType.DEADLINE, representative.getId(), representative.getTitle(), null));
        }

        log.info("ProductPost 스케줄러 작동 완료 - 진행 중 변경: {}건, 종료 변경: {}건", doingCount, endCount);
    }
}