package com.zero9platform.domain.searchLog.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.util.SearchProfanityFilter;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.searchLog.elasticsearch.ProductDocument;
import com.zero9platform.domain.searchLog.model.response.RecentSearchResponse;
import com.zero9platform.domain.searchLog.model.response.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.repository.ProductPostSearchRepository;
//import com.zero9platform.domain.searchLog.repository.SearchLogElasticsearchRepository;
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
    private final ProductPostSearchRepository productPostSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final SearchProfanityFilter searchProfanityFilter;
    private final StringRedisTemplate redisTemplate;
    private final SearchLogManager searchLogManager;
    private final ObjectMapper objectMapper;

    /**
     * 통합 검색 API (상품명, 인플루언서 닉네임, 본문 통합 검색)
     */
    @Transactional(readOnly = true)
    public Page<SearchLogItemResponse> searchLog(String cleanKeyword, String postType, Pageable pageable, Authentication authentication, HttpServletRequest request) {

        // 1. 사용자 신분 식별 (회원인 경우 ID, 비회원은 IP 활용)
        AuthUser authUser = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getPrincipal() instanceof AuthUser) {
                authUser = (AuthUser) authentication.getPrincipal();
            }
        }

        // 2. 검색 파라미터 유효성 검증
        validateSearchCondition(postType);

        // 필수 입력값 체크
        if (cleanKeyword == null || cleanKeyword.isEmpty()) {
            return Page.empty(pageable);
        }

        // 3. 비속어 차단 (금지어 포함 시 즉시 예외 발생)
        if (searchProfanityFilter.isBadWord(cleanKeyword)) {
            log.warn("비속어 필터링에 걸린 검색어: [{}]", cleanKeyword);

            throw new CustomException(ExceptionCode.SEARCH_LOGS_PROFANITY_NOT_ALLOWED, cleanKeyword);
        }

        // 4. 어뷰징 방지 (1분 이내 동일 키워드 반복 검색 시 랭킹 합산 제외용)
        String identifier = (authUser != null) ? String.valueOf(authUser.getId()) : getClientIp(request);
        boolean isAbuse = isDuplicateSearch(cleanKeyword, identifier);

        // 5. Elasticsearch 검색 쿼리 조립
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    // 검색 목적에 따른 타겟 필드 및 가중치 설정 (부스팅)
                    List<String> targetFields = switch (postType != null ? postType : "all") {
                        case "product_title" ->
                                List.of("title^15", "keyword^10");               // 제목 검색 시 ngram 필드(keyword) 가중치 강화
                        case "content" -> List.of("content^1");                              // 내용레 1배 가중치
                        case "influencer" -> List.of("nickname^10");                         // 닉네임 10배 가중치
                        default ->
                                List.of("title^20", "keyword^10", "nickname^10", "content^1"); // 전체 검색 시: 제목(정확도) > 키워드(부분일치) > 닉네임 > 내용
                    };

                    // A. 검색어 포함 여부 확인 (필수 조건)
                    b.must(m -> m.multiMatch(mm -> mm
                            .fields(targetFields)
                            .query(cleanKeyword)
                            .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                            .fuzziness("AUTO") // 오타 자동 보정 허용
                    ));

                    // B. 정확도 가산점 (선택 조건: 제목과 정확히 일치하면 점수 뻥튀기)
                    b.should(s -> s.match(ma -> ma
                            .field("title")
                            .query(cleanKeyword)
                            .boost(10.0f) // 제목 가중치 20에 더해, 정확히 일치하면 10점을 더 얹어줌
                    ));
                    return b;
                }))
                // 6. 정렬 기준: 1순위 정확도 점수, 2순위 최신 등록순
                .withSort(s -> s.score(sc -> sc.order(SortOrder.Desc))) // 1순위: 유사도 점수 높은 순 (정확도)
                .withSort(s -> s.field(f -> f.field("startDate").order(SortOrder.Desc))) // 2순위: 점수가 같을 경우 최신 시작일 순
                .withPageable(pageable)
                .build();

        // 7. ES 검색 실행 및 결과 매핑
        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);

        // 8. 부가 데이터 연동 (검색 결과 중 PRODUCT 타입의 찜 개수 일괄 조회)
        List<Long> postIds = hits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .filter(doc -> "PRODUCT".equals(doc.getPostType()))
                .map(ProductDocument::getNumericId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, Long> favCounts = favoriteCountMap(postIds);

        // 9. ES 결과(SearchDocument)를 Response DTO로 변환
        List<SearchLogItemResponse> contents = hits.getSearchHits().stream()
                .map(hit -> {
                    ProductDocument doc = hit.getContent();
                    String matchType = determineMatchType(doc, cleanKeyword, postType);
                    return SearchLogItemResponse.from(doc, matchType, favCounts.getOrDefault(doc.getNumericId(), 0L), cleanKeyword);
                })
                .toList();

        // 10. 검색 로그 저장 및 검색 랭킹 카운터 증가 (유저 개인 히스토리 저장)
        Long userId = (authUser != null) ? authUser.getId() : null;

        searchLogManager.record(cleanKeyword, userId, identifier, isAbuse);   // 개별 유저 최근 검색어용

        return new PageImpl<>(contents, pageable, hits.getTotalHits());
    }

    /**
     * 개인별 최근 검색어 목록 조회 (Redis 활용)
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
     * 실시간 자동완성 제안 (입력한 글자로 시작하는 상품명 추출)
     */
    @Transactional(readOnly = true)
    public List<String> showAutoComplete(String keyword) {

        // 상품/공구 통합 마스터 인덱스
        List<ProductDocument> results = productPostSearchRepository.findByKeywordStartingWith(keyword);

        // 검색어(keyword)만 중복 없이 뽑아서 리스트로 반환
        return results.stream()
                .map(ProductDocument::getTitle)
                .distinct()
                .limit(10) // 10개만 보여주기
                .toList();
    }

    /**
     * 클라이언트 실제 IP 추출 (Nginx 프록시 환경 대응)
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
     * 중복 검색 여부 확인 (1분 이내 동일 키워드 체크)
     * 어뷰징 체크 (회원 ID 또는 비회원 IP 활용)
     */
    private boolean isDuplicateSearch(String cleanKeyword, String identifier) {

        // 키 예시: CHECK:SEARCH:127.0.0.1:아이폰
        String checkKey = "ZERO9:SEARCH:CHECK:SEARCH:" + identifier + ":" + cleanKeyword;

        // 1분간 동일 식별자의 동일 키워드 기록 유지
        // 최초 검색 시에만 true 반환 (이후 1분간은 false)
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(checkKey, "true", Duration.ofMinutes(1));

        return Boolean.FALSE.equals(isFirst);
    }

    /**
     * 매칭 타입 결정 로직 분리
     * 검색어가 어떤 항목(제목/내용/닉네임)에 걸렸는지 판별
     */
    private String determineMatchType(ProductDocument doc, String cleanKeyword, String postType) {

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
     * 검색 요청 카테고리 유효성 검사
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