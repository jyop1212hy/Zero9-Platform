package com.zero9platform.domain.searchLog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero9platform.domain.ranking.service.RankingCounter;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.response.RecentSearchResponse;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchLogManager {

    private final SearchLogRepository searchLogRepository;
    private final StringRedisTemplate redisTemplate;
    private final RankingCounter rankingCounter;
    private final ObjectMapper objectMapper;

    // 검색 완료 후 호출되는 통합 기록 메서드
    @Async("SEARCH_LOG")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String keyword, Long userId, String identifier, boolean isAbuse) {

        if (isAbuse || keyword == null || keyword.isBlank()) {
            return;
        }

        // DB 로그 저장
        searchLogRepository.save(new SearchLog(keyword, userId));

        // 랭킹 카운트 증가
        rankingCounter.increaseKeyword(keyword);

        // Redis 최근 검색어 저장
        saveRecentSearch(keyword, identifier, userId);
    }

    /**
     * 최근 검색어 저장
     */
    private void saveRecentSearch(String keyword, String identifier, Long userId) {

        // 키 결정: 로그인 유저면 'USER:ID', 비회원이면 'IP:IP주소'
        String key = (userId != null) ? "ZERO9:SEARCH:RECENT:USER:" + userId : "ZERO9:SEARCH:RECENT:IP:" + identifier;

        try {
            RecentSearchResponse newLog = new RecentSearchResponse(keyword, LocalDateTime.now());
            String jsonValue = objectMapper.writeValueAsString(newLog);
            List<String> currentList = redisTemplate.opsForList().range(key, 0, 9);

            if (currentList != null) {
                for (String item : currentList) {
                    if (item.contains("\"keyword\":\"" + keyword + "\"")) {
                        redisTemplate.opsForList().remove(key, 1, item);
                    }
                }
            }

            redisTemplate.opsForList().leftPush(key, jsonValue);    // 리스트의 맨 앞에 추가
            redisTemplate.opsForList().trim(key, 0, 9);   // 최근 검색어를 10개까지만 유지
            redisTemplate.expire(key, Duration.ofDays(7));          // 최근 검색어 데이터도 7일 정도 지나면 자동 삭제(TTL)
        } catch (JsonProcessingException e) {
            log.error("최근 검색어 처리 중 직렬화 오류: {}", e.getMessage());
        }
    }
}