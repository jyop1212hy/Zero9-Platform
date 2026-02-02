package com.zero9platform.domain.ranking.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupPurchasePostRankingListResponse {

    private final int rank;
    private final Long groupPurchasePostId;
    private final String groupPurchasePostTitle;
    private final Long viewCount;

    public static GroupPurchasePostRankingListResponse from(int rank, Long groupPurchasePostId, String groupPurchasePostTitle, Long viewCount) {
        return new GroupPurchasePostRankingListResponse(
                rank,
                groupPurchasePostId,
                groupPurchasePostTitle,
                viewCount
        );
    }
}
