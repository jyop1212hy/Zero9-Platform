package com.zero9platform.domain.activity_feed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class ActivityFeedRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ActivityFeedRedisService(StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;

        // Jackson 날짜 직렬화 에러 방지 설정
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // LocalDateTime 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 포맷 유지
    }

    private static final String FEED_LIST_KEY_PREFIX = "user:feed:";
    private static final String ORDER_COUNT_KEY_PREFIX = "product:order:count:";

    /**
     * 피드 목록 캐시 조회 (역직렬화)
     */
    public List<ActivityFeedResponse> getCachedFeeds(String key) {

        String json = redisTemplate.opsForValue().get(FEED_LIST_KEY_PREFIX + key);

        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<ActivityFeedResponse>>() {});
        } catch (JsonProcessingException e) {

            log.error("피드 캐시 역직렬화 실패: {}", e.getMessage());

            return null;
        }
    }

    /**
     * 피드 목록 캐시 저장 (직렬화)
     */
    public void saveFeedCache(String key, List<ActivityFeedResponse> feeds) {

        try {

            String json = objectMapper.writeValueAsString(feeds);

            redisTemplate.opsForValue().set(FEED_LIST_KEY_PREFIX + key, json, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            log.error("피드 캐시 직렬화 실패: {}", e.getMessage());
        }
    }

    /**
     * 실시간 주문 카운팅 (Atomic INCR)
     */
    public void incrementOrderCount(Long productId) {

        String key = ORDER_COUNT_KEY_PREFIX + productId;
        Long count = redisTemplate.opsForValue().increment(key);

        // 첫 카운트 발생 시 만료 시간 설정 (당일 23:59:59)
        if (count != null && count == 1) {

            LocalDateTime midnight = LocalDateTime.now().with(LocalTime.MAX);

            redisTemplate.expireAt(key, midnight.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    /**
     * 실시간 주문 카운트 조회
     */
    public long getOrderCount(Long productId) {

        String key = ORDER_COUNT_KEY_PREFIX + productId;

        String countStr = redisTemplate.opsForValue().get(key);

        return countStr != null ? Long.parseLong(countStr) : 0L;
    }

    /**
     * 피드 캐시 삭제 (Evict)
     */
    public void deleteFeedCache(String key) {
        redisTemplate.delete(FEED_LIST_KEY_PREFIX + key);
    }
}