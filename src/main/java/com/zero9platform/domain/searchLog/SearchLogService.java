package com.zero9platform.domain.searchLog;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.SearchLogListResponse;
import com.zero9platform.domain.searchLog.model.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final ProductPostRepository productPostRepository;
    /**
     * 키워드 통합 검색
     */
    @Transactional
    public Page<SearchLogItemResponse> searchLog(String keyword, String searchCondition, Pageable pageable) {

        // 검색어 예외 처리
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(ExceptionCode.INVALID_KEYWORD);
        }

        // searchCondition 예외 처리
        if (searchCondition != null && !"product_title".equals(searchCondition) && !"product_name".equals(searchCondition) && !"influencer".equals(searchCondition)) {
            throw new CustomException(ExceptionCode.CATEGORY_FALSE);
        }

        // 통합 검색
        // category(product_title, product_name, influencer), 없으면 셋 다 포함하여 검색
        Page<ProductPost> searchResult = productPostRepository.searchByKeyword(keyword, searchCondition, pageable);

        // 검색어 로그 저장 (검색 카운트 증가)
        searchKeywordSave(keyword);

        // searchResult로부터 gppId 추출
        List<Long> gppIdList = new ArrayList<>();
        for (ProductPost post : searchResult) {
            gppIdList.add(post.getId());
        }

        // 찜 개수 조회 -> Map 변환
        Map<Long, Long> favoriteCountMap = new HashMap<>();
        for (Object[] row : productPostFavoriteRepository.countByGppIdList(gppIdList)) {
            favoriteCountMap.put((Long) row[0], (Long) row[1]);
        }

        // DTO 변환
        List<SearchLogItemResponse> dtoList = new ArrayList<>();
        for (ProductPost post : searchResult.getContent()) {
            dtoList.add(SearchLogItemResponse.from(post, favoriteCountMap.getOrDefault(post.getId(), 0L)));
        }

        return new PageImpl<>(dtoList, searchResult.getPageable(), searchResult.getTotalElements());
    }

    /**
     * 인기 검색어 차트(공동구매 상품명)
     */
    @Transactional(readOnly = true)
    public List<SearchLogListResponse> searchLogProductNameList() {
        return searchLogRepository.findTopKeywords(PageRequest.of(0, 10) );
    }


    /**
     * 검색 키워드 로그 저장
     */
    private void searchKeywordSave(String keyword) {

        // 검색어가 없으면 로그 저장하지 않음
        if (keyword == null || keyword.isBlank()) {
            return;
        }

        // 기존 검색어가 있으면 조회, 없으면 새로 생성
        SearchLog searchLog = searchLogRepository.findByKeyword(keyword)
                .orElseGet(() -> new SearchLog(keyword));

        // 검색 횟수 증가
        searchLog.increaseCount();

        //DB 저장
        searchLogRepository.save(searchLog);
    }
}
