package com.zero9platform.domain.searchLog.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.util.SearchProfanityFilter;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.ranking.service.RankingCounter;
import com.zero9platform.domain.searchLog.model.response.RecentSearchResponse;
import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.response.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.repository.SearchDocumentRepository;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;
    private final ProductPostRepository productPostRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final SearchDocumentRepository searchDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RankingCounter rankingCounter;
    private final SearchProfanityFilter searchProfanityFilter;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 통합 검색 API
     * 검색 대상 - 공동구매 상품명, 인플루언서 활동 닉네임
     */
    @Transactional
    public Page<SearchLogItemResponse> searchLog(String cleanKeyword, String postType, Pageable pageable, AuthUser authUser, HttpServletRequest request) {

        // 검색 조건 검증 (허용되지 않은 조건 차단)
        validateSearchCondition(postType);

        // 필수 입력값 체크
        if (cleanKeyword == null || cleanKeyword.isEmpty()) {
            return Page.empty(pageable);
        }

        // 비속어 필터링 (원본 cleanKeyword로 사용자에게 피드백)
        if (searchProfanityFilter.isBadWord(cleanKeyword)) {
            log.warn("비속어 필터링에 걸린 검색어: [{}]", cleanKeyword);
            throw new CustomException(ExceptionCode.PROFANITY_NOT_ALLOWED, cleanKeyword);
        }

        // 식별자 결정: 로그인 유저 ID 또는 비회원 IP (동일 키워드를 1분 내 재검색 시 로그/랭킹 제외)
        String identifier = (authUser != null) ? String.valueOf(authUser.getId()) : getClientIp(request);
        boolean isAbuse = isDuplicateSearch(cleanKeyword, identifier);


        // ES용 쿼리 빌드 (DB 레포지토리 호출 대신)
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> {
                    List<String> targetFields = switch (postType != null ? postType : "all") {
                        case "product_title" -> targetFields = List.of("title", "content");
                        case "content" -> targetFields = List.of("content");
                        case "influencer" -> targetFields = List.of("nickname");
                        default -> List.of("title", "content", "nickname"); // 전체 검색
                    };
                    return m.fields(targetFields)
                            .query(cleanKeyword)
                            .fuzziness("1");
                }))
                .withSort(s -> s.field(f -> f.field("startDate").order(SortOrder.Desc)))
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
        if (!isAbuse) {
            Long userId = (authUser != null) ? authUser.getId() : null;
            saveSearchLogs(cleanKeyword, userId);              // 검색로그 저장
            rankingCounter.increaseKeyword(cleanKeyword); // 정제된 키워드로 랭킹 반영
            saveRecentSearch(cleanKeyword, identifier, userId);   // 개별 유저 최근 검색어용
        } else {
            log.info("어뷰징 의심으로 랭킹 반영 생략: id={}, keyword={}", identifier, cleanKeyword);
        }
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
        if (rawHistory == null) return List.of();

        // "키워드|날짜"를 분리하여 DTO로 변환
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

        if ("influencer".equals(postType)) return "인플루언서 매칭";
        if ("product_title".equals(postType)) return "제목 매칭";
        if ("content".equals(postType)) return "내용 매칭";

        String nickname = Objects.requireNonNullElse(doc.getNickname(), "");
        String title = Objects.requireNonNullElse(doc.getTitle(), "");
        if (nickname.contains(cleanKeyword)) return "인플루언서 매칭";
        if (title.contains(cleanKeyword)) return "제목 매칭";
        return "내용 매칭";
    }

    /**
     * 검색 로그 저장
     */
    @Transactional
    public void saveSearchLogs(String cleanKeyword, Long userId) {

        // 검색어가 없으면 로그 저장하지 않음
        if (cleanKeyword == null || cleanKeyword.isBlank()) {
            return;
        }
        // 검색어 로그 저장
        searchLogRepository.save(new SearchLog(cleanKeyword, userId));
    }

    /**
     * 최근 검색어 저장
     */
    public void saveRecentSearch(String cleanKeyword, String identifier, Long userId) {

        // 키 결정: 로그인 유저면 'USER:ID', 비회원이면 'IP:IP주소'
        String key = (userId != null) ? "ZERO9:SEARCH:RECENT:USER:" + userId : "ZERO9:SEARCH:RECENT:IP:" + identifier;

        try {
            RecentSearchResponse newLog = new RecentSearchResponse(cleanKeyword, LocalDateTime.now());

            String jsonValue = objectMapper.writeValueAsString(newLog);

            List<String> currentList = redisTemplate.opsForList().range(key, 0, -1);
            if (currentList != null) {
                for (String item : currentList) {
                    RecentSearchResponse oldLog = objectMapper.readValue(item, RecentSearchResponse.class);
                    if (oldLog.getKeyword().equals(cleanKeyword)) {
                        redisTemplate.opsForList().remove(key, 1, item);
                    }
                }
            }

            // 리스트의 맨 앞에 추가
            redisTemplate.opsForList().leftPush(key, jsonValue);

            // 최근 검색어를 10개까지만 유지
            redisTemplate.opsForList().trim(key, 0, 9);

            // 최근 검색어 데이터도 7일 정도 지나면 자동 삭제(TTL)
            redisTemplate.expire(key, Duration.ofDays(7));
        } catch (JsonProcessingException e) {
            log.error("최근 검색어 직렬화 실패: {}", e.getMessage());
        }
    }

    /**
     * 찜 개수 조회
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> favoriteCountMap(List<Long> posts) {

        if (posts.isEmpty()) return Map.of();

        // DB 집계 결과를 Map 형태로 변환
        return productPostFavoriteRepository.countByGppIdList(posts)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // productPostId
                        row -> (Long) row[1]));   // favoriteCount
    }

    /**
     * 검색 조건 검증
     */
    public void validateSearchCondition(String condition) {
        if (condition == null || condition.isBlank()) return;
        List<String> allowedConditions = List.of("product_title", "product_name", "influencer", "content");
        if (!allowedConditions.contains(condition)) {
            throw new CustomException(ExceptionCode.CATEGORY_FALSE);
        }
    }

    /**
     * DB의 모든 데이터를 ES로 전송 (Bulk Indexing)
     */
    @Async("SEARCH_LOG")
    @Transactional(readOnly = true)
    public void bulkIndexing() {

        log.info("[Bulk Indexing] 시작");

        int pageSize = 1000;

        // ProductPost 인덱싱 처리
        int productPage = 0;
        long totalProducts = 0;

        while (true) {
            Page<ProductPost> productSlice = productPostRepository.findAll(PageRequest.of(productPage, pageSize));

            if (productSlice.isEmpty()) break;

            List<SearchDocument> productDocs = productSlice.getContent().stream()
                    .filter(productPost -> productPost.getUser().getDeletedAt() == null)
                    .filter(productPost -> productPost.getId() != null)
                    .map(post -> SearchDocument.builder()
                            .id("PRODUCT_POST_" + post.getId())
                            .postType("PRODUCT_POST")
                            .title(post.getTitle())
                            .content(post.getContent())
                            .nickname(post.getUser().getNickname())
                            .price(post.getOriginalPrice())
                            .image(post.getImage())
                            .startDate(post.getStartDate())
                            .endDate(post.getEndDate())
                            .userId(post.getUser().getId())
                            .build())
                    .toList();

            try {
                searchDocumentRepository.saveAll(productDocs);
                totalProducts += productDocs.size();
                log.info("[ProductPost] {} 건 인덱싱 중...", totalProducts);
            } catch (Exception e) {
                log.error("{}번 페이지 인덱싱 실패: {}", productPage, e.getMessage());
            }
            productPage++;
        }

        // GroupPurchasePost 페이징 처리
        int gppPage = 0;
        long totalGpps = 0;
        while (true) {
            Page<GroupPurchasePost> gppSlice = groupPurchasePostRepository.findAll(PageRequest.of(gppPage, pageSize));

            if (gppSlice.isEmpty()) break;

            List<SearchDocument> gppDocs = gppSlice.getContent().stream()
                    .filter(groupPurchasePost -> groupPurchasePost.getUser() != null && groupPurchasePost.getUser().getDeletedAt() == null)
                    .filter(groupPurchasePost -> groupPurchasePost.getId() != null && groupPurchasePost.getDeletedAt() == null)
                    .map(gpp -> SearchDocument.builder()
                            .id("GROUP_PURCHASE_POST_" + gpp.getId())
                            .postType("GROUP_PURCHASE_POST")
                            .title(gpp.getProductName())
                            .content(gpp.getContent())
                            .nickname(gpp.getUser().getNickname())
                            .price(gpp.getPrice())
                            .image(gpp.getImage())
                            .startDate(gpp.getStartDate())
                            .endDate(gpp.getEndDate())
                            .userId(gpp.getUser().getId())
                            .build())
                    .toList();

            try {
                searchDocumentRepository.saveAll(gppDocs);
                totalGpps += gppDocs.size();
                log.info("[GroupPurchasePost] {} 건 인덱싱 중...", totalGpps);
            } catch (Exception e) {
                log.error("{}번 GPP 페이지 인덱싱 실패: {}", gppPage, e.getMessage());
            }
            gppPage++;
        }
        log.info("[Bulk Indexing] 완료! (총합: {} 건)", (totalProducts + totalGpps));
    }


}
