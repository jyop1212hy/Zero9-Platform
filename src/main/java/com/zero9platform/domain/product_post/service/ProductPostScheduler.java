package com.zero9platform.domain.product_post.service;

import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductPostScheduler {

    private final ProductPostRepository productPostRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateProductPostProgress() {

        LocalDateTime now = LocalDateTime.now();

        int doingCount = productPostRepository.updateToDoing(now);
        int endCount = productPostRepository.updateToEnd(now);

        log.info("ProductPost 스케줄러 작동 완료 - 진행 중 변경: {}건, 종료 변경: {}건", doingCount, endCount);
    }
}