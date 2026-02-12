package com.zero9platform.domain.activity_feed.repository;

import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ActivityFeedRepository extends JpaRepository<ActivityFeed, Long> {

    // 공용 피드만 조회 (전체 피드 목록용)
    Page<ActivityFeed> findByUserIdIsNull(Pageable pageable);

    // 피드 중복 생성 방지를 위한 존재 확인
    boolean existsByTypeAndTargetIdAndUserId(String type, Long targetId, Long userId);

    // 공용인지 개인피드인지 구분
    Optional<ActivityFeed> findFirstByTypeAndTargetIdAndUserIdOrderByUpdatedAtDesc(String type, Long targetId, Long userId);

    // 찜이 없는 유저
    Page<ActivityFeed> findByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);

    // 피드 개인화
    // 내 피드 조회
    @Query("SELECT f FROM ActivityFeed f " +
            "WHERE (f.targetId IN :favoriteList AND f.userId IS NULL) " +
            "OR (f.userId = :userId) " +
            "ORDER BY f.updatedAt DESC")
    Page<ActivityFeed> findFeedsByFavoriteListOrUserId(
            @Param("favoriteList") List<Long> favoriteList,
            @Param("userId") Long userId,
            Pageable pageable
    );
}