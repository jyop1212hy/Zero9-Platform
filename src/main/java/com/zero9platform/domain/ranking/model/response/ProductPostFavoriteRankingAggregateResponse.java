package com.zero9platform.domain.ranking.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductPostFavoriteRankingAggregateResponse {

    private final Long productPostId;
    private final String title;
    private final Long favoriteCount;
}
