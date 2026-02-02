package com.zero9platform.domain.searchLog.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.searchLog.entity.SearchContext;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.model.SearchLogListResponse;
import com.zero9platform.domain.searchLog.repository.SearchContextRepository;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;
    private final SearchContextRepository searchContextRepository;
    private final ProductPostRepository productPostRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;

    /**
     * 통합 검색 API
     * 검색 대상 - 공동구매 상품명, 인플루언서 활동 닉네임
     */
    @Transactional
    public Page<SearchLogItemResponse> searchLog(String keyword, String searchCondition, Pageable pageable, AuthUser authUser) {

        // 검색 조건 검증 (허용되지 않은 조건 차단)
        validateSearchCondition(searchCondition);

        // 통합 검색 category(product_title, product_name, influencer), 없으면 셋 다 포함하여 검색
        Page<ProductPost> searchResult = productPostRepository.searchByKeyword(keyword, searchCondition, pageable);

        // 검색 결과 없으면 바로 반환
        if (searchResult.isEmpty()) {
            return Page.empty(pageable);
        }

        // 검색 결과 게시물들의 찜 개수 조회
        Map<Long, Long> favoriteCountMap = getFavoriteCountMap(searchResult.getContent());

        // DTO 변환
        Page<SearchLogItemResponse> resultPage = searchResult.map(post ->
                SearchLogItemResponse.from(
                        post,
                        favoriteCountMap.getOrDefault(post.getId(), 0L)
                )
        );

        // 검색 로그 저장
        // - 비회원: 검색어 로그만 저장
        // - 회원  : 검색어 로그 + 검색 컨텍스트 저장
        Long userId = (authUser != null) ? authUser.getId() : null;
        saveSearchLogs(keyword, searchResult.getContent(), userId);

        return resultPage;
    }

    /**
     * 검색 조건 검증
     */
    @Transactional
    public void validateSearchCondition(String condition) {

        if (condition == null || condition.isBlank()) {return;}

        if (!"product_title".equals(condition) && !"product_name".equals(condition) && !"influencer".equals(condition)) {
            throw new CustomException(ExceptionCode.CATEGORY_FALSE);
        }
    }

    /**
     * 검색 로그 + 컨텍스트 저장
     */
    @Transactional
    public void saveSearchLogs(String keyword, List<ProductPost> posts, Long userId) {

        // 검색어가 없으면 로그 저장하지 않음
        if (keyword == null || keyword.isBlank()) {
            return;
        }

        // 검색어 로그 저장 (카운트 증가)
        SearchLog searchLog = searchLogRepository.findByKeyword(keyword)
                .orElseGet(() -> new SearchLog(keyword));

        // 검색 횟수 증가
        searchLog.increaseCount();

        searchLogRepository.save(searchLog);

        // 로그인 유저가 아니면 컨텍스트 저장 안 함
        if (userId == null || posts.isEmpty()) {
            return;
        }

        // 검색 결과 게시물 기준으로 컨텍스트 생성
        List<SearchContext> contexts = posts
                .stream()
                .map(post -> new SearchContext(keyword, post.getId(), userId))
                .toList();

        // 일괄 저장
        searchContextRepository.saveAll(contexts);
    }

    /**
     * 찜 개수 조회
     */
    @Transactional
    public Map<Long, Long> getFavoriteCountMap(List<ProductPost> posts) {

        // 게시물 ID 추출
        List<Long> ids = posts.stream()
                .map(ProductPost::getId)
                .toList();

        // DB 집계 결과를 Map 형태로 변환
        return productPostFavoriteRepository.countByGppIdList(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // productPostId
                        row -> (Long) row[1]   // favoriteCount
                ));
    }
}
