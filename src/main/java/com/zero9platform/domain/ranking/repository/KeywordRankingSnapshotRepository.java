package com.zero9platform.domain.ranking.repository;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.domain.ranking.entity.KeywordRankingSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRankingSnapshotRepository extends JpaRepository<KeywordRankingSnapshot, Long> {

    Page<KeywordRankingSnapshot> findByPeriodOrderByKeywordCountDesc(RankingPeriod period, Pageable pageable);
}
