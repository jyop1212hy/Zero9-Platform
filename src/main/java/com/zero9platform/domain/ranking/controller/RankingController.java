package com.zero9platform.domain.ranking.controller;

import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.ranking.model.response.*;
import com.zero9platform.domain.ranking.service.RankingService;
import com.zero9platform.domain.ranking.model.response.ProductPostFavoriteRankingListResponse;
import com.zero9platform.domain.ranking.model.response.SearchLogRankingListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/zero9")
public class RankingController {

    private final RankingService rankingService;

    /**
     * 인기 검색어 랭킹 (클릭로그 or 키워드 조회수)
     */
    @GetMapping("/ranking/searchLog")
    public ResponseEntity<CommonResponse<List<SearchLogRankingListResponse>>> searchLogKeywordRankingHandler(@RequestParam(required = false) RankingPeriod period) {

        List<SearchLogRankingListResponse> searchLogRankingListResponse = rankingService.searchKeywordRanking(period);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("인기 검색어 랭킹 조회 성공", searchLogRankingListResponse));
    }

    /**
     * 실시간 공동구매 상품 게시물 랭킹(찜) (유저용 - 실시간)
     */
    @GetMapping("/ranking/favorite")
    public ResponseEntity<CommonResponse<List<ProductPostFavoriteRankingListResponse>>> productPostFavoriteRankingHandler(@RequestParam(required = false) RankingPeriod period) {

        List<ProductPostFavoriteRankingListResponse> productPostFavoriteRankingListResponse = rankingService.favoriteRanking(period);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 판매 게시물 랭킹 조회 성공", productPostFavoriteRankingListResponse));
    }
}


