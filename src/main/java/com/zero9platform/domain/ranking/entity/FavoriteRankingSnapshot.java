package com.zero9platform.domain.ranking.entity;

import com.zero9platform.common.enums.RankingPeriod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "favorite_ranking_snapshots")
public class FavoriteRankingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 게시물 ID
    @Column(name = "product_post_id", nullable = false)
    private Long productPostId;

    // 랭킹 기준 기간
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RankingPeriod period;

    // 집계된 찜 개수 (캐시 스냅샷 값)
    @Column(nullable = false)
    private Long favoriteCount;

    // 스냅샷 생성 시각
    @Column(nullable = false)
    private LocalDateTime snapshotAt;

    public FavoriteRankingSnapshot(Long productPostId, RankingPeriod period, Long favoriteCount) {
        this.productPostId = productPostId;
        this.period = period;
        this.favoriteCount = favoriteCount;
        this.snapshotAt = LocalDateTime.now();
    }
}
