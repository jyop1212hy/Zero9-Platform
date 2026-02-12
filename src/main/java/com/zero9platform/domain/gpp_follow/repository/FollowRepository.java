package com.zero9platform.domain.gpp_follow.repository;

import com.zero9platform.domain.gpp_follow.entity.GppFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<GppFollow, Long> {

    /**
     * 구독 관계 (사용자, 공동구매 게시물) 확인
     */
    boolean existsByUserIdAndGroupPurchasePostId(Long userId, Long groupPurchasePostId);

    /**
     * 구독 관계 (사용자, 공동구매 게시물) 반환
     */
    Optional<GppFollow> findByUserIdAndGroupPurchasePostId(Long userId, Long groupPurchasePostId);
}
