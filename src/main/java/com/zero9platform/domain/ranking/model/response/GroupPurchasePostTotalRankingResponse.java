package com.zero9platform.domain.ranking.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupPurchasePostTotalRankingResponse {

    private final int rank;
    private final Long groupPurchasePostId;
    private final String productName;
    private final long totalViewCount;

    public static GroupPurchasePostTotalRankingResponse from(int rank, Long groupPurchasePostId, String productName, long totalViewCount
    ) {
        return new GroupPurchasePostTotalRankingResponse(
                rank,
                groupPurchasePostId,
                productName,
                totalViewCount
        );
    }

}
