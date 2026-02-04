package com.zero9platform.domain.ranking.repository;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.domain.ranking.entity.FavoriteRankingSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRankingSnapshotRepository extends JpaRepository<FavoriteRankingSnapshot, Long> {

    Page<FavoriteRankingSnapshot> findByPeriodOrderByFavoriteCountDesc(RankingPeriod period, Pageable pageable);
}
