package com.zero9platform.domain.ranking.repository;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.domain.ranking.entity.SearchLogRankingSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRankingSnapshotRepository extends JpaRepository<SearchLogRankingSnapshot, Long> {

    // 검색어 랭킹 조회
    Page<SearchLogRankingSnapshot> findByPeriodAndTargetDateOrderByKeywordCountDesc(RankingPeriod period, String targetDate, Pageable pageable);
}
