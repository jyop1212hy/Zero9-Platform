package com.zero9platform.domain.ranking.scheduler;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.domain.ranking.entity.FavoriteRankingSnapshot;
import com.zero9platform.domain.ranking.entity.KeywordRankingSnapshot;
import com.zero9platform.domain.ranking.repository.FavoriteRankingSnapshotRepository;
import com.zero9platform.domain.ranking.repository.KeywordRankingSnapshotRepository;
import com.zero9platform.domain.ranking.service.RankingCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class RankingFlushScheduler {

    private final RankingCounter rankingCounter;
    private final FavoriteRankingSnapshotRepository favoriteRankingSnapshotRepository;
    private final KeywordRankingSnapshotRepository keywordRankingSnapshotRepository;

    /**
     * DAILY 랭킹 스냅샷 저장 - 실행 시점: 매일 자정 00시
     */
    @Transactional
    @Scheduled(cron = "0 5 0 * * *")
    public void snapshotDaily() {

        saveSnapshotAndClear(RankingPeriod.DAILY, LocalDateTime.now().minusDays(1));
    }

    /**
     * WEEKLY 스냅샷 저장 - 실행 시점: 매주 월요일 자정 00시
     */
    @Transactional
    @Scheduled(cron = "0 5 0 * * MON")
    public void snapshotWeekly() {

        saveSnapshotAndClear(RankingPeriod.WEEKLY, LocalDateTime.now().minusWeeks(1));
    }

    /**
     * MONTHLY 스냅샷 저장 - 실행 시점: 매월 1일 자정 00시
     */
    @Transactional
    @Scheduled(cron = "0 15 0 1 * *") // 매월 1일 00:15
    public void snapshotMonthly() {

        saveSnapshotAndClear(RankingPeriod.MONTHLY, LocalDateTime.now().minusMonths(1));
    }

    /**
     * 스냅샷 저장 및 캐시 초기화
     */
    private void saveSnapshotAndClear(RankingPeriod period, LocalDateTime targetTime) {

        String favoriteRedisKey = rankingCounter.buildRedisKey("FAVORITE", period, targetTime);
        String searchRedisKey = rankingCounter.buildRedisKey("SEARCH", period, targetTime);

        // 이 시점의 날짜 패턴 미리 추출 (중복 호출 방지)
        String targetDate = rankingCounter.dateRedisKey(period, targetTime);

        // 상품 찜 랭킹 스냅샷 저장
        Set<ZSetOperations.TypedTuple<String>> favoriteRankings = rankingCounter.findSnapshotKey(favoriteRedisKey);
        if (!favoriteRankings.isEmpty()) {
            List<FavoriteRankingSnapshot> favoriteSnapshotList = favoriteRankings.stream()
                    .map(tuple -> new FavoriteRankingSnapshot(
                            Long.parseLong(tuple.getValue()),
                            period,
                            tuple.getScore().longValue(),
                            targetDate))
                    .toList();
            favoriteRankingSnapshotRepository.saveAll(favoriteSnapshotList);
            rankingCounter.deleteKey(favoriteRedisKey);
        }

        // 검색어 랭킹 스냅샷 저장
        Set<ZSetOperations.TypedTuple<String>> keywordRankings = rankingCounter.findSnapshotKey(searchRedisKey);
        if (!keywordRankings.isEmpty()) {
            List<KeywordRankingSnapshot> keywordSnapshotList = keywordRankings.stream()
                    .map(tuple -> new KeywordRankingSnapshot(
                            tuple.getValue(),
                            period,
                            tuple.getScore().longValue(),
                            targetDate))
                    .toList();
            keywordRankingSnapshotRepository.saveAll(keywordSnapshotList);
            rankingCounter.deleteKey(searchRedisKey);
        }

        log.info("[RANKING-FLUSH] {} 정산 완료. 대상: {}, 저장건수: (찜:{}, 검색:{})", period, targetDate, favoriteRankings.size(), keywordRankings.size());
    }
}