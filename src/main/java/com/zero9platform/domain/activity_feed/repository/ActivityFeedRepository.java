package com.zero9platform.domain.activity_feed.repository;

import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {

    // 동일한 건으로 피드 생성 방지 (중복방지)
    boolean existsByTypeAndProductPostId(String type, Long productPostId);
}
