package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupPurchasePostViewCountScheduler {

    private final StringRedisTemplate redisTemplate;
    private final GroupPurchasePostRepository groupPurchasePostRepository;

    private static final String VIEW_COUNT_KEY_PREFIX = "gpp:view_count:";

    @Transactional
    @Scheduled(fixedDelay = 60_000) // 1분
    public void syncViewCountToDb() {

        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) continue;

            Long delta = Long.valueOf(value);
            Long gppId = Long.valueOf(key.replace(VIEW_COUNT_KEY_PREFIX, ""));

            // DB 반영
            groupPurchasePostRepository.increaseViewCountBatch(gppId, delta);

            // Redis 초기화
            redisTemplate.delete(key);
        }

        log.info("GPP ViewCount batch sync 완료 - 대상 수: {}", keys.size());
    }

}
