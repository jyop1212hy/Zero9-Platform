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

    public static SearchLogItemResponse from(GroupPurchasePost post, Long favoriteCount) {
        return new SearchLogItemResponse(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getImage(),
                post.getProductName(),
                post.getPrice(),
                post.getViewCount(),
                favoriteCount,
                post.getStartDate(),
                post.getEndDate()
        );
    }


}
