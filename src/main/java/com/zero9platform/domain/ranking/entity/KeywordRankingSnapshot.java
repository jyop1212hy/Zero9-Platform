package com.zero9platform.domain.ranking.entity;

import com.zero9platform.common.enums.RankingPeriod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "keyword_ranking_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordRankingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    // 랭킹 기준 기간
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RankingPeriod period;

    // 집계된 찜 개수 (캐시 스냅샷 값)
    @Column(nullable = false)
    private Long keywordCount;

    // 스냅샷 생성 시각
    @Column(nullable = false)
    private LocalDateTime snapshotAt;

    public KeywordRankingSnapshot(String keyword, RankingPeriod period, Long keywordCount) {
        this.keyword = keyword;
        this.period = period;
        this.keywordCount = keywordCount;
        this.snapshotAt = LocalDateTime.now();
    }
}
