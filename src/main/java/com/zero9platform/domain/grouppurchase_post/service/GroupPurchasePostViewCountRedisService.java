package com.zero9platform.domain.grouppurchase_post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupPurchasePostViewCountRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String VIEW_COUNT_KEY_PREFIX = "gpp:view_count:";
    private static final String VIEW_COUNT_KEY_SET = "gpp:view_count:keys";

    private static final String TOTAL_RANKING_KEY = "gpp:ranking:total";
    private static final String DAILY_RANKING_KEY_PREFIX = "gpp:ranking:daily:";

    /**
     * 조회수 증가 (Redis)
     * - 쓰기 캐싱 (개념적으로는 버퍼링과 유사하지만 다름)
     */
    public long increaseViewCountRedisCache(Long gppId) {

        String key = VIEW_COUNT_KEY_PREFIX + gppId;
//        redisTemplate.opsForValue().increment(key); // INCR는 Redis atomic (원자성이므로, 동시성을 제어)
        // 조회수 증가 (증가 후 값 반환)
        Long incrementedValue = redisTemplate.opsForValue().increment(key);

        // 스케줄러가 찾을 수 있도록 gppId를 집합에 등록
        redisTemplate.opsForSet().add(VIEW_COUNT_KEY_SET, gppId.toString());

        // 전체 누적 랭킹 증가
        redisTemplate.opsForZSet().incrementScore(TOTAL_RANKING_KEY, gppId.toString(), 1);

        // 오늘의 실시간 랭킹 증가
        String dailyKey = getTodayRankingKey();
        redisTemplate.opsForZSet().incrementScore(dailyKey, gppId.toString(), 1);

        // 오늘의 실시간 랭킹 TTL 설정
        // TTL이 없을 때만 설정
        Long ttl = redisTemplate.getExpire(dailyKey);
        if (ttl == null || ttl == -1) {
            setDailyRankingTTL(dailyKey);
        }

        // 조회수 증가값 반환
        return incrementedValue != null ? incrementedValue : 0L;
    }

    /**
     * 조회수 조회 (Redis)
     * - 아직 DB에 반영되지 않은 값(cached) 읽기
     * 조회수 증가 구조변경에 의해 사용안함.
     */
    public long getCachedViewCount(Long gppId) {
        String key = VIEW_COUNT_KEY_PREFIX + gppId;
        String cached = redisTemplate.opsForValue().get(key);

        // Redis에 value가 없으면 0 반환
        return cached != null ? Long.parseLong(cached) : 0L;
    }

    /**
     * 오늘 랭킹 Key
     * 예: gpp:ranking:daily:20260206
     */
    private String getTodayRankingKey() {
        return DAILY_RANKING_KEY_PREFIX + LocalDate.now();
    }

    /**
     * 일일 실시간 랭킹 TTL
     * 항상 내일 00시에 만료
     */
    private void setDailyRankingTTL(String dailyKey) {

        // 현재 시각
        LocalDateTime now = LocalDateTime.now();
        // 내일 시각 = 내일 00시
        LocalDateTime tomorrow = now.toLocalDate().plusDays(1).atStartOfDay();

        // 남은 만료 시간 = 현재 ~ 내일 00시
        long secondsUntilMidnight = Duration.between(now, tomorrow).getSeconds();

        // 일일 키값당 만료시간 설정
        redisTemplate.expire(dailyKey, Duration.ofSeconds(secondsUntilMidnight));
    }

}
