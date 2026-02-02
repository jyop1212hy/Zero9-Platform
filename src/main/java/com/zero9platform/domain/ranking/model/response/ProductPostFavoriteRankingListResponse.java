package com.zero9platform.domain.ranking.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductPostFavoriteRankingListResponse {

    private final int rank;
    private final Long productPostId;
    private final String title;
    private final Long favoriteCount;

    public static ProductPostFavoriteRankingListResponse from(int rank, Long productPostId, String title, Long favoriteCount) {
        return new ProductPostFavoriteRankingListResponse(
                rank,
                productPostId,
                title,
                favoriteCount
        );
    }
}
