package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupPurchasePostStatusScheduler {

    private final GroupPurchasePostRepository groupPurchasePostRepository;

    /**
     * 매일 00시에 모집 상태 자동 변경
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // cron 내부적으로 이전 실행이 끝난 후 다음 스케줄을 처리
    public void updateGppProgressStatus() {

        log.info("GPP 모집상태 변경 시작, 실행 스레드명 : {}", Thread.currentThread().getName());

        LocalDateTime now = LocalDateTime.now();

        int readyToDoing = groupPurchasePostRepository.updateReadyToDoing(now);
        int doingToEnd = groupPurchasePostRepository.updateDoingToEnd(now);

        log.info("GPP 모집상태 변경 - READY->DOING: {}, DOING->END: {}", readyToDoing, doingToEnd);
    }
}