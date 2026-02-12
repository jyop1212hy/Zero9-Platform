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

    // 추가 : 조회수 관리용 key 집합
    private static final String VIEW_COUNT_KEY_SET = "gpp:view_count:keys";

    /**
     * 조회수를 DB에 실제 반영 -> 일괄로 업데이트 하도록 스케줄링
     */
    @Transactional
    @Scheduled(fixedDelay = 60_000) // 1분
    public void syncViewCountToDb() {

        log.info("실행 스레드명 : {}", Thread.currentThread().getName());

        // 기존 get() + delete() 방식은 중간에 증가하면 유실 가능
        // getAndSet으로 0으로 초기화하면서 기존 값 가져옴 (원자적 처리)
        // 단점 : getAndSet은 key를 유지하기 때문에, 안쓰는 key에 의해 메모리를 점유당하게 됨
        // 보완 : 조회수 증가가 발생한 gppId 집합으로 데이터 관리 (조회수 증가수가 0이면 관리대상에서 제외 = 삭제)

        // 추가 : 조회수 증가가 발생한 gppId 집합 조회
        Set<String> gppIds = redisTemplate.opsForSet().members(VIEW_COUNT_KEY_SET);

        if (gppIds == null || gppIds.isEmpty()) {
            return;
        }

        for (String gppIdStr : gppIds) {
            String key = VIEW_COUNT_KEY_PREFIX + gppIdStr;

            // 각 gpp의 조회수 추출 + 완료체크(0)
            // getAndSet으로 원자적 처리
            String value = redisTemplate.opsForValue().getAndSet(key, "0");

            if (value == null) {
                // value(key값->조회수)가 없으면 집합에서도 제거
                redisTemplate.opsForSet().remove(VIEW_COUNT_KEY_SET, gppIdStr);

                continue;
            }

            // cached = [조회수 증가수]
            // 0 여부 검사를 위한 타입변환
            long cached = Long.parseLong(value);

            if (cached == 0L) {
                // 0이면 key + 집합 모두 정리
                redisTemplate.delete(key);
                redisTemplate.opsForSet().remove(VIEW_COUNT_KEY_SET, gppIdStr);

                continue;
            }

            // 그래도 남아있는 gppId는 반영 대상
            Long gppId = Long.valueOf(gppIdStr);

            // DB 반영
            groupPurchasePostRepository.increaseViewCountBatch(gppId, cached);

            // 반영 후 key 완전 삭제
            redisTemplate.delete(key);

            // 집합에서도 제거
            redisTemplate.opsForSet().remove(VIEW_COUNT_KEY_SET, gppIdStr);
        }

        log.info("GPP 조회수 동기화 완료 - 대상 수: {}", gppIds.size());
    }
}