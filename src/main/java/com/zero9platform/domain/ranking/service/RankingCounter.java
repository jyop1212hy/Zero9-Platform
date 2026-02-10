package com.zero9platform.domain.ranking.service;

import com.zero9platform.common.enums.RankingPeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class RankingCounter {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "ZERO9:RANKING";
    private static final String SEARCH = "SEARCH";
    private static final String FAVORITE = "FAVORITE";

    /**
     * 검색 키워드 카운터 증가
     */
    public void increaseKeyword(String keyword) {

        for (RankingPeriod period : RankingPeriod.values()) {
            if (period == RankingPeriod.REALTIME) continue;

            String key = nowCreateRedisKey(SEARCH, period);
            redisTemplate.opsForZSet().incrementScore(key, keyword, 1);

            // 기간별 TTL 설정 (중복 코드 분리)
            switch (period) {
                case DAILY -> redisTemplate.expire(key, Duration.ofDays(3));    // 일간(DAILY) 데이터라면 2~3일 뒤 자동 삭제
                case WEEKLY -> redisTemplate.expire(key, Duration.ofDays(14));  // 주간 데이터는 2주 뒤 삭제
                case MONTHLY -> redisTemplate.expire(key, Duration.ofDays(45)); // 월간 데이터는 한 달 정산 기간을 고려해 넉넉히 45일 정도로 설정

            }
        }
    }

    /**
     * 공동구매 상품 찜 카운터 차감
     */
    public void increaseFavorite(Long productPostId) {

        for (RankingPeriod period : RankingPeriod.values()) {
            if (period == RankingPeriod.REALTIME) continue;

            String key = nowCreateRedisKey(FAVORITE, period);
            redisTemplate.opsForZSet().incrementScore(key, productPostId.toString(), 1);

            // 기간별 TTL 설정 (중복 코드 분리)
            switch (period) {
                case DAILY -> redisTemplate.expire(key, Duration.ofDays(3));    // 일간(DAILY) 데이터라면 2~3일 뒤 자동 삭제
                case WEEKLY -> redisTemplate.expire(key, Duration.ofDays(14));  // 주간 데이터는 2주 뒤 삭제
                case MONTHLY -> redisTemplate.expire(key, Duration.ofDays(45)); // 월간 데이터는 한 달 정산 기간을 고려해 넉넉히 45일 정도로 설정
            }
        }
    }

    /**
     * 공동구매 상품 찜 카운터 감소
     */
    public void decreaseFavorite(Long productPostId) {

        for (RankingPeriod period : RankingPeriod.values()) {
            if (period == RankingPeriod.REALTIME) continue;

            String key = nowCreateRedisKey(FAVORITE, period);
            Double score = redisTemplate.opsForZSet().incrementScore(key, productPostId.toString(), -1);

            if (score != null && score <= 0) {
                redisTemplate.opsForZSet().remove(key, productPostId.toString());
            }
        }
    }

    /**
     * 랭킹 조회 (Service에서 사용)
     */
    public Set<ZSetOperations.TypedTuple<String>> topRankings(String type, RankingPeriod period, int limit) {

        String key = (period == RankingPeriod.REALTIME) ? nowCreateRedisKey(type, RankingPeriod.DAILY) : nowCreateRedisKey(type, period);
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);
    }

    /**
     * 스냅샷용 조회 (특정 시점의 키를 조회하기 위함)
     */
    public Set<ZSetOperations.TypedTuple<String>> findSnapshotKey(String key) {

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    /**
     * 캐시메모리 초기화
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 현재 시점의 키 생성
     */
    public String nowCreateRedisKey(String type, RankingPeriod period) {
        return buildRedisKey(type, period, LocalDateTime.now());
    }

    /**
     * 날짜 패턴을 포함한 키 생성 로직
     */
    public String buildRedisKey(String type, RankingPeriod period, LocalDateTime dateTime) {

        // 날짜 패턴 생성
        String datePattern = dateRedisKey(period, dateTime);

        // 최종 키 생성: {서비스접두어}:{타입}:{기간}:{날짜}
        // 결과 예시: ZERO9:RANKING:SEARCH:DAILY:2026-02-08
        return String.format("%s:%s:%s:%s", KEY_PREFIX, type, period.name(), datePattern);
    }

    /**
     * DB 저장용 날짜 패턴 생성 (과거 이력 조회용 컬럼값)
     */
    public String dateRedisKey(RankingPeriod period, LocalDateTime targetTime) {
        return switch (period) {
            case DAILY -> targetTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));   // 2026-02-08
            case WEEKLY -> targetTime.format(DateTimeFormatter.ofPattern("yyyyWww"));     // 2026W06 (6주차)
            case MONTHLY -> targetTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));    // 2026-02
            default -> targetTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        };
    }
}
