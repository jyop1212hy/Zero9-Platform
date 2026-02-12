package com.zero9platform.domain.activity_feed.service;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.repository.ActivityFeedRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityFeedService {

    private final ActivityFeedRepository feedRepository;
    private final ProductPostFavoriteRepository favoriteRepository;
    private final ActivityFeedRedisService redisService; // Redis 캐시 및 카운팅 전담 서비스

    /**
     * [생성 로직] 피드 생성 및 Redis 상태 관리
     * - DB에는 변하지 않는 '뼈대(타입, 대상ID)'만 저장합니다.
     * - 실시간 카운트와 리스트 캐시를 Redis에서 즉시 처리하여 Write 성능을 높입니다.
     */
    @Transactional
    public void feedCreate(FeedType type, Long targetId, String targetName, Long userId) {

        log.info("[ActivityFeed] 개인 피드 생성 시도 - 유저: {}, 상품: {}", userId, targetId);

        // 개인 피드  동일 유저에게 동일 타입/대상의 피드가 중복 생성되지 않도록 체크 후 저장
        if (!feedRepository.existsByTypeAndTargetIdAndUserId(type.name(), targetId, userId)) {

            feedRepository.save(new ActivityFeed(type.name(), targetName, targetId, userId));

            log.info("[ActivityFeed] 개인 피드 저장 완료");
        }

        // 실시간 카운팅: Redis의 Atomic INCR 연산을 사용하여 동시성 이슈 없이 구매자 수를 집계
        // 공용 집계형 피드 모든 유저가 볼 "N명 주문 중" 피드 관리
        if (type.isHasCounter()) {

            redisService.incrementOrderCount(targetId);

            long currentCount = redisService.getOrderCount(targetId);

            log.info("[ActivityFeed] 실시간 카운트 업데이트 - 상품: {}, 현재: {}명", targetId, currentCount);

            // DB에는 상품당 딱 1개의 'PAYMENT_COUNT' 레코드만 유지 (userId는 null로 설정)
            ActivityFeed aggFeed = feedRepository.findFirstByTypeAndTargetIdAndUserIdOrderByUpdatedAtDesc(FeedType.PAYMENT_COUNT.name(), targetId, null)
                    .orElseGet(() -> new ActivityFeed(FeedType.PAYMENT_COUNT.name(), targetName, targetId, null));

            // updateAt을 강제로 갱신하여 목록 상단으로 올림 (Dirty Checking 활용)
            if (aggFeed.getId() != null) {
                aggFeed.updateUpdatedAt(targetName);

                log.info("[ActivityFeed] 기존 공용 피드 시간 갱신 ");
            } else {
                log.info("[ActivityFeed] 신규 공용 집계 피드 생성");
            }

            feedRepository.save(aggFeed);
        }

        // 캐시 무효화(Cache Evict): 데이터가 변했으므로 기존 캐시를 삭제
        redisService.deleteFeedCache(String.valueOf(userId));
        redisService.deleteFeedCache("all");
    }

    /**
     * [조회 로직] 전체 피드 목록 조회 - 개인피드 x
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> feedsGetList(Pageable pageable) {

        // 1. Redis 캐시 확인
        String cacheKey = "all:" + pageable.getPageNumber();

        List<ActivityFeedResponse> cached = redisService.getCachedFeeds(cacheKey);

        if (cached != null && !cached.isEmpty()) {

            log.info("[전체피드] Redis 캐시 히트! 데이터를 즉시 반환합니다.");

            return new PageImpl<>(cached, pageable, cached.size());
        }

        // 캐시 없으면 DB 전체 조회
        Page<ActivityFeedResponse> responsePage = feedRepository.findByUserIdIsNull(pageable)
                .map(entity -> {
                    FeedType t = FeedType.valueOf(entity.getType());

                    long count = t.isHasCounter() ? redisService.getOrderCount(entity.getTargetId()) : 0L;

                    return ActivityFeedResponse.from(entity, count);
                });

        // Redis에 전체 피드 캐시 저장
        if (!responsePage.isEmpty()) {

            log.info("[전체피드] DB 데이터를 Redis에 캐싱합니다.");

            redisService.saveFeedCache(cacheKey, responsePage.getContent());
        }

        return responsePage;
    }


    /**
     * [조회 로직] 내 피드 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ActivityFeedResponse> feedsGetMyList(Long userId, Pageable pageable) {

        // 캐시에 데이터가 있다면 아래 DB 로직을 통째로 스킵
        List<ActivityFeedResponse> cached = redisService.getCachedFeeds(String.valueOf(userId));

        if (cached != null && !cached.isEmpty()) {

            log.info("Redis에서 데이터를 즉시 반환합니다. userId: {}", userId);

            return new org.springframework.data.domain.PageImpl<>(cached, pageable, cached.size());
        }

        // 유저의 관심(찜) 상품 ID 리스트 확보
        List<Long> favoriteList = favoriteRepository.findProductPostIdsByUserId(userId);

        Page<ActivityFeed> feedPage;

        // DB 조회
        feedPage = favoriteList.isEmpty() ? feedRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable) : feedRepository.findFeedsByFavoriteListOrUserId(favoriteList, userId, pageable);

        // 실시간 조립
        Page<ActivityFeedResponse> responsePage = feedPage.map(entity -> {

            FeedType type = FeedType.valueOf(entity.getType());

            long count = type.isHasCounter() ? redisService.getOrderCount(entity.getTargetId()) : 0L;

            return ActivityFeedResponse.from(entity, count);
        });

        // Redis에 캐시 저장
        if (!responsePage.isEmpty()) {
            redisService.saveFeedCache(String.valueOf(userId), responsePage.getContent());
        }

        return responsePage;
    }
}