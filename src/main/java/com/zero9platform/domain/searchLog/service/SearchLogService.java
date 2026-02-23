package com.zero9platform.domain.searchLog.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.util.SearchProfanityFilter;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.searchLog.model.response.RecentSearchResponse;
import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import com.zero9platform.domain.searchLog.model.response.SearchLogItemResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final SearchProfanityFilter searchProfanityFilter;
    private final StringRedisTemplate redisTemplate;
    private final SearchLogManager searchLogManager;
    private final ObjectMapper objectMapper;

    /**
     * 통합 검색 API
     * 검색 대상 - 공동구매 상품명, 인플루언서 활동 닉네임
     */
    @Transactional(readOnly = true)
    public Page<SearchLogItemResponse> searchLog(String cleanKeyword, String postType, Pageable pageable, Authentication authentication, HttpServletRequest request) {

        // 서비스 내부에서 자체적으로 AuthUser 추출 (안전한 신분 확인)
        AuthUser authUser = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getPrincipal() instanceof AuthUser) {
                authUser = (AuthUser) authentication.getPrincipal();
            }
        }

        // 검색 조건 검증 (허용되지 않은 조건 차단)
        validateSearchCondition(postType);

        // 필수 입력값 체크
        if (cleanKeyword == null || cleanKeyword.isEmpty()) {
            return Page.empty(pageable);
        }

        // 비속어 필터링 (원본 cleanKeyword로 사용자에게 피드백)
        if (searchProfanityFilter.isBadWord(cleanKeyword)) {
            log.warn("비속어 필터링에 걸린 검색어: [{}]", cleanKeyword);

            throw new CustomException(ExceptionCode.SEARCH_LOGS_PROFANITY_NOT_ALLOWED, cleanKeyword);
        }

        // 식별자 및 어뷰징 체크: 로그인 유저 ID 또는 비회원 IP (동일 키워드를 1분 내 재검색 시 로그/랭킹 제외)
        String identifier = (authUser != null) ? String.valueOf(authUser.getId()) : getClientIp(request);

        boolean isAbuse = isDuplicateSearch(cleanKeyword, identifier);

        // ES용 쿼리 빌드 (필드 가중치 적용 제목:5, 내용:1)
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    List<String> targetFields = switch (postType != null ? postType : "all") {
                        case "product_title" -> List.of("title^10", "content^1");    // 제목에 10배 가중치
                        case "content" -> List.of("content^1");                 // 내용레 1배 가중치
                        case "influencer" -> List.of("nickname^10");            // 닉네임 10배 가중치
                        default -> List.of("title^10", "nickname^10", "content^1"); // 전체 검색 시 제목 > 닉네임 > 내용 순
                    };

                    // 2. MultiMatch 쿼리 최적화
                    b.must(m -> m.multiMatch(mm -> mm
                            .fields(targetFields)
                            .query(cleanKeyword)
                            // 제목(title)처럼 중요한 필드에서 정확도가 높아야 하므로
                            // 가중치가 높은 필드 위주로 점수를 계산하도록 유도
                            .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                            // 오타 허용 범위를 조절 (단어 길이가 짧을 땐 오타 허용 안 함)
                            .fuzziness("AUTO")
                    ));
                    return b;
                }))
                // 검색 정렬 순위
                .withSort(s -> s.score(sc -> sc.order(SortOrder.Desc))) // 1순위: 유사도 점수 높은 순 (정확도)
                .withSort(s -> s.field(f -> f.field("startDate").order(SortOrder.Desc))) // 2순위: 점수가 같을 경우 최신 시작일 순
                .withPageable(pageable)
                .build();

        // ES 검색 실행
        SearchHits<SearchDocument> hits = elasticsearchOperations.search(query, SearchDocument.class);

        // 데이터 가공 (ID 추출 및 찜수 매핑)
        List<Long> postIds = hits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .filter(doc -> "PRODUCT_POST".equals(doc.getPostType()))
                .map(SearchDocument::getNumericId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, Long> favCounts = favoriteCountMap(postIds);

        // ES 결과(SearchDocument)를 Response DTO로 변환
        List<SearchLogItemResponse> contents = hits.getSearchHits().stream()
                .map(hit -> {
                    SearchDocument doc = hit.getContent();
                    String matchType = determineMatchType(doc, cleanKeyword, postType);
                    return SearchLogItemResponse.from(doc, matchType, favCounts.getOrDefault(doc.getNumericId(), 0L));
                })
                .toList();

        // 검색 로그 저장 및 검색 랭킹 카운터 증가
        Long userId = (authUser != null) ? authUser.getId() : null;

        searchLogManager.record(cleanKeyword, userId, identifier, isAbuse);   // 개별 유저 최근 검색어용

        return new PageImpl<>(contents, pageable, hits.getTotalHits());
    }

    /**
     * 나의 최근 검색 히스토리 조회
     */
    @Transactional(readOnly = true)
    public List<RecentSearchResponse> getMySearchHistory(AuthUser authUser, HttpServletRequest request) {

        // 저장할 때와 동일한 규칙으로 키를 생성
        String identifier = getClientIp(request);
        String key = (authUser != null) ? "ZERO9:SEARCH:RECENT:USER:" + authUser.getId() : "ZERO9:SEARCH:RECENT:IP:" + identifier;

        List<String> rawHistory = redisTemplate.opsForList().range(key, 0, 9);

        if (rawHistory == null) {
            return List.of();
        }

        return rawHistory.stream()
                .map(item -> {
                    try {
                        return objectMapper.readValue(item, RecentSearchResponse.class);
                    } catch (JsonProcessingException e) {
                        log.error("최근 검색어 역직렬화 실패: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * IP 추출 유틸리티 (Nginx 등 프록시 환경 고려)
     */
    private String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return (ip != null && ip.contains(",")) ? ip.split(",")[0].trim() : ip;
    }

    /**
     * 어뷰징 체크 (회원 ID 또는 비회원 IP 활용)
     */
    private boolean isDuplicateSearch(String cleanKeyword, String identifier) {

        // 키 예시: CHECK:SEARCH:127.0.0.1:아이폰
        String checkKey = "ZERO9:SEARCH:CHECK:SEARCH:" + identifier + ":" + cleanKeyword;

        // 1분간 동일 식별자의 동일 키워드 기록 유지
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(checkKey, "true", Duration.ofMinutes(1));

        return Boolean.FALSE.equals(isFirst);
    }

    /**
     * 매칭 타입 결정 로직 분리
     */
    private String determineMatchType(SearchDocument doc, String cleanKeyword, String postType) {

        if ("influencer".equals(postType)) {
            return "인플루언서 매칭";
        }

        if ("product_title".equals(postType)) {
            return "제목 매칭";
        }

        if ("content".equals(postType)) {
            return "내용 매칭";
        }

        String nickname = Objects.requireNonNullElse(doc.getNickname(), "");
        String title = Objects.requireNonNullElse(doc.getTitle(), "");

        if (nickname.contains(cleanKeyword)) {
            return "인플루언서 매칭";
        }

        if (title.contains(cleanKeyword)) {
            return "제목 매칭";
        }

        return "내용 매칭";
    }

    /**
     * 찜 개수 조회
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> favoriteCountMap(List<Long> posts) {

        if (posts.isEmpty()) {
            return Map.of();
        }

        // DB 집계 결과를 Map 형태로 변환
        return productPostFavoriteRepository.countByGppIdList(posts)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // productPostId
                        row -> (Long) row[1])  // favoriteCount
                );
    }

    /**
     * 검색 조건 검증
     */
    @Transactional(readOnly = true)
    public void validateSearchCondition(String condition) {

        if (condition == null || condition.isBlank()) {
            return;
        }

        List<String> allowedConditions = List.of("product_title", "product_name", "influencer", "content");

        if (!allowedConditions.contains(condition)) {
            throw new CustomException(ExceptionCode.SEARCH_LOGS_INVALID_CATEGORY);
        }
    }
}