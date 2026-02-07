package com.zero9platform.domain.grouppurchase_post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupPurchasePostViewCountRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String VIEW_COUNT_KEY_PREFIX = "gpp:view_count:";
    private static final String VIEWED_KEY_PREFIX = "gpp:viewed:";

    /**
     * 조회수 증가 (Redis)
     */
    public void increaseViewCountRedis(Long gppId) {
        String key = VIEW_COUNT_KEY_PREFIX + gppId;
        redisTemplate.opsForValue().increment(key); // INCR는 Redis atomic (원자성이므로, 동시성을 제어)
    }

    /**
     * 조회수 조회 (Redis)
     * - 아직 DB에 반영되지 않은 값(Delta) 읽기
     */
    public long getViewCountDelta(Long gppId) {
        String key = VIEW_COUNT_KEY_PREFIX + gppId;
        String value = redisTemplate.opsForValue().get(key);

        // Redis에 value가 없으면 0 반환
        return value != null ? Long.parseLong(value) : 0L;
    }

//    /**
//     * 조회수 증가 (중복 조회 방지 ver)
//     */
//    public void increaseViewCountRedis(Long gppId, Long userId) {
//        // DB에 접근 하지않으며, 원자적으로 조회수를 증가(동시성 발생 X)
//
//        // 어떤 gpp를 누가 봤는지
//        String viewedKey = VIEWED_KEY_PREFIX + ":" + gppId + ":" + userId;
//
//        // 중복 조회 방지
//        Boolean isFirstView = redisTemplate.opsForValue().setIfAbsent(viewedKey, "1", Duration.ofHours(24));
//
//        // 이미 조회한 사용자의 경우
//        if (Boolean.FALSE.equals(isFirstView)) {
//            return;
//        }
//        // 그렇지 않은 경우
//        redisTemplate.opsForValue().increment(VIEW_COUNT_KEY_PREFIX + ":" + gppId);
//
//    }

}
