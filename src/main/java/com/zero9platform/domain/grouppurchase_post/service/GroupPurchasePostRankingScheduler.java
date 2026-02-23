package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupPurchasePostRankingScheduler {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * 서버가 완전히 실행된 후, 딱 한번 실행
     * 서버 가동 시 DB를 기준으로 랭킹용 Redis 메모리를 구성해줄 필요가 있음
     */
//    @PostConstruct // Bean 생성+의존성 주입 직후 실행, 스케줄러가 등록되기 전에 실행될 가능성 존재
    @EventListener(ApplicationReadyEvent.class) // 완전 가동 후 실행
    public void init() {
        refreshTotalRanking();
    }

    /**
     * 매일 DB로부터 Redis Total 랭킹 새로고침
     * 매 10분마다 갱신
     * DB 부하를 덜기 위해 인덱싱 적용
     */
    @Scheduled(cron = "0 */10 * * * *") // cron 내부적으로 이전 실행이 끝난 후 다음 스케줄을 처리
    public void refreshTotalRanking() {

        log.info("GPP 누적 랭킹 갱신, 실행 스레드명 : {}", Thread.currentThread().getName());

        // 기존 랭킹 ZSet삭제
        redisTemplate.delete("gpp:ranking:total");

        // gpp 풀스캔, 자동순위정렬 ZSet에 추가
        groupPurchasePostRepository.findAll().forEach(gpp -> {
            redisTemplate.opsForZSet().add(
                    "gpp:ranking:total",
                    gpp.getId().toString(),
                    gpp.getViewCount()
            );
        });
    }
}