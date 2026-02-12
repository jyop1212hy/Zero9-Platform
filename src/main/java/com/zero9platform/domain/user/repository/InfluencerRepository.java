package com.zero9platform.domain.user.repository;

import com.zero9platform.domain.user.entity.Influencer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InfluencerRepository extends JpaRepository<Influencer, Long> {

    /**
     * 인플루언서 목록 조회
     */
    @Query("""
        SELECT i FROM Influencer i JOIN i.user u
        WHERE (:approved IS NULL OR i.influencerApprovalStatus = :approved) AND (:nickname IS NULL OR u.nickname LIKE CONCAT('%', :nickname, '%')) AND u.role = 'INFLUENCER' 
        ORDER BY u.createdAt DESC
    """)
    Page<Influencer> findByApprovalStatusAndUser(@Param("approved") Boolean approved, @Param("nickname") String nickname, Pageable pageable);

    /**
     * 인플루언서 user_id 조회
     */
    Optional<Influencer> findByUserId(Long userId);

    /**
     * 승인되지 않은 인플루언서 존재 여부 확인
     */
    boolean existsByUserIdAndInfluencerApprovalStatusFalse(Long userId);
}