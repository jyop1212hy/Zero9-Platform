package com.zero9platform.domain.ranking.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchLogRankingListResponse {

    private final int rank;
    private final String keyword;
    private final Long count;

    public static SearchLogRankingListResponse from(int rank, String keyword, Long count) {
        return new SearchLogRankingListResponse(rank, keyword, count);

    }
}
