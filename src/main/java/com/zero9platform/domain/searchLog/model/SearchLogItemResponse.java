package com.zero9platform.domain.searchLog.model;

import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SearchLogItemResponse {

    private final Long productPostId;
    private final Long userId;
    private final String nickname;
    private final String image;
    private final String productPostTitle;
    private final Long productPrice;
    private final Long favoriteCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static SearchLogItemResponse from(ProductPost productPost, Long favoriteCount) {
        return new SearchLogItemResponse(
                productPost.getId(),
                productPost.getUser().getId(),
                productPost.getUser().getNickname(),
                productPost.getImage(),
                productPost.getTitle(),
                productPost.getProduct().getProductPrice(),
                favoriteCount,
                productPost.getStartDate(),
                productPost.getEndDate()
        );
    }


}
