package com.zero9platform.domain.searchLog.model;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SearchLogItemResponse {

    private final Long gppId;
    private final Long userId;
    private final String nickname;
    private final String image;
    private final String productName;
    private final Long price;
    private final Long viewCount;
    private final Long favoriteCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static SearchLogItemResponse from(GroupPurchasePost groupPurchasePost, Long favoriteCount) {
        return new SearchLogItemResponse(
                groupPurchasePost.getId(),
                groupPurchasePost.getUser().getId(),
                groupPurchasePost.getUser().getNickname(),
                groupPurchasePost.getImage(),
                groupPurchasePost.getProductName(),
                groupPurchasePost.getPrice(),
                groupPurchasePost.getViewCount(),
                favoriteCount,
                groupPurchasePost.getStartDate(),
                groupPurchasePost.getEndDate()
        );
    }


}
