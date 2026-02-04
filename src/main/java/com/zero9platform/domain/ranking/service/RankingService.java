package com.zero9platform.domain.ranking.service;

import com.zero9platform.common.enums.ProgressStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.ranking.model.response.*;
import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final SearchLogRepository searchLogRepository;
    private final ProductPostFavoriteRepository productPostFavoriteRepository;

    /**
     * 공동구매 게시물 랭킹 (조회수 기준)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostRankingListResponse> groupPurchasePostRanking() {

        // 조회수 기준 상위 10개 공동구매 게시물 조회
        List<GroupPurchasePost> posts = groupPurchasePostRepository.findTop10ByDeletedAtIsNullOrderByViewCountDesc();

        AtomicInteger rank = new AtomicInteger(1);

        // 집계 결과를 랭킹 응답 DTO로 변환
        return posts.stream()
                .map(aggregate -> GroupPurchasePostRankingListResponse.from(
                                rank.getAndIncrement(),
                                aggregate.getId(),
                                aggregate.getProductName(),
                                aggregate.getViewCount())
                )
                .toList();
    }

    /**
     * 상품판매 게시물 랭킹 (찜 기준)
     */
    @Transactional(readOnly = true)
    public List<ProductPostFavoriteRankingListResponse> productPostFavoriteRanking() {

        // 찜 개수 기준 상위 10개 상품 게시물 집계 조회
        List<ProductPostFavoriteRankingAggregateResponse> favorite = productPostFavoriteRepository
                .findTop10ProductPostByFavorite(ProgressStatus.DOING, PageRequest.of(0, 10));

        AtomicInteger rank = new AtomicInteger(1);

        // 집계 결과를 랭킹 응답 DTO로 변환
        return favorite.stream()
                .map(aggregate ->
                        ProductPostFavoriteRankingListResponse.from(
                                rank.getAndIncrement(),
                                aggregate.getProductPostId(),
                                aggregate.getTitle(),
                                aggregate.getFavoriteCount())
                )
                .toList();
    }

    /**
     * 인기 검색어 랭킹 (키워드 조회수)
     * 고도화 이후 클릭로그집계로 변경
     */
    @Transactional(readOnly = true)
    public List<SearchLogRankingListResponse> searchLogKeywordRanking() {

        // 검색 횟수 기준 상위 10개 키워드 조회
        List<SearchLogRankingAggregateResponse> searchLogs = searchLogRepository
                .findTopKeywords(PageRequest.of(0, 10));

        AtomicInteger rank = new AtomicInteger(1);

        // 검색어 집계 결과를 랭킹 응답 DTO로 변환
        return searchLogs.stream()
                .map(aggregate ->
                        SearchLogRankingListResponse.from(
                                rank.getAndIncrement(),
                                aggregate.getKeyword(),
                                aggregate.getCount())
                )
                .toList();
    }
}
