package com.zero9platform.domain.activity_feed.repository;

import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {

    // 동일한 건으로 피드 생성 방지 (중복방지)
    boolean existsByTypeAndTargetId(String type, Long targetId);

    // 피드 개인화
    // 내 피드 조회
    @Query("SELECT f FROM ActivityFeed f WHERE f.targetId IN :favoriteList ORDER BY f.createdAt DESC")
    Page<ActivityFeed> findFeedsByFavoriteList(@Param("favoriteList") List<Long> favoriteList, Pageable pageable);
}
