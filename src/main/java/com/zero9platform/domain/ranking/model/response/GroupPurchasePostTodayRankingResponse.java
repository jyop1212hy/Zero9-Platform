package com.zero9platform.domain.ranking.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupPurchasePostTodayRankingResponse {

    private final int rank;
    private final Long groupPurchasePostId;
    private final String productName;
    private final long todayViewCount;

    public static GroupPurchasePostTodayRankingResponse from(int rank, Long groupPurchasePostId, String productName, long todayViewCount
    ) {
        return new GroupPurchasePostTodayRankingResponse(
                rank,
                groupPurchasePostId,
                productName,
                todayViewCount
        );
    }

}
