package com.zero9platform.domain.ranking.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchLogRankingAggregateResponse {

    private final String keyword;
    private final Long count;
}
