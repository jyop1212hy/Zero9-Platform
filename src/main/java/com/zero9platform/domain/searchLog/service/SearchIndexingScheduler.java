package com.zero9platform.domain.searchLog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchIndexingScheduler {

    private final SearchIndexer searchIndexer;

    /**
     * 매일 새벽 3시에 최근 24시간 변경분만 동기화
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void runDailyIncrementalIndexing() {

        log.info("정기 벌크 인덱싱 스케줄러 실행");

        searchIndexer.bulkIndexingIncremental();
    }
}