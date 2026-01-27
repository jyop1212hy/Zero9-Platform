package com.zero9platform.domain.activity_feed.service;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.repository.ActivityFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityFeedService {

    private final ActivityFeedRepository feedRepository;

    /**
     * 피드 생성
     */
    @Transactional
    public void feedCreate(String type, Long productPostId, String productName) {

        // 중복 방지(이미 있으면 패스)
        if (feedRepository.existsByTypeAndProductPostId(type, productPostId)) {
            return;
        }

        String message = "";
        if (type.equals(FeedType.POPULAR.name())) {
            message = "[" + productName + "] 상품이 많은 관심을 받고 있습니다.";
        } else if (type.equals(FeedType.PAYMENT.name())) {
            message = "[" + productName + "] 상품의 새로운 주문이 접수되었습니다.";
        } else if (type.equals(FeedType.DEADLINE.name())) {
            message = "[" + productName + "] 상품의 모집 마감이 임박하였습니다.";
        } else if (type.equals(FeedType.LOW_STOCK.name())) {
            message = "[" + productName + "] 상품의 재고가 소진될 예정입니다.";
        }else if (type.equals(FeedType.SOLD_OUT.name())) {
            message = "[" + productName + "] 상품이 성황리에 품절되었습니다!";
        }else if (type.equals(FeedType.SOON.name())) {
            message = "[" + productName + "] 상품이 곧 공개될 예정입니다.";
        }

        // 유저id 필드 null이면 전체피드 (현재 개인별 X)
        ActivityFeed feed = new ActivityFeed(type, message, productPostId, null);
        feedRepository.save(feed);
    }

    /**
     * 피드 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ActivityFeedResponse> feedsGetList() {
        // 조회
        List<ActivityFeed> feeds = feedRepository.findAll();

        // 빈 리스트 생성
        List<ActivityFeedResponse> responses = new ArrayList<>();

        // 향상된 for문으로 하나씩 DTO 변환 후 추가
        for (ActivityFeed feed : feeds) {
            responses.add(ActivityFeedResponse.from(feed));
        }

        return responses;
    }
}
