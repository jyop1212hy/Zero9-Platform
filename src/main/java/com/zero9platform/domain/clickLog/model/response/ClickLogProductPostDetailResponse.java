package com.zero9platform.domain.clickLog.model.response;

import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ClickLogProductPostDetailResponse {

    private final Long productPostId;
    private final String title;
    private final String content;
//    private final Integer stock;
    private final String image;
    private final String category;
    private final String productPostProgressStatus;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static ClickLogProductPostDetailResponse from(ProductPost productPost) {
        return new ClickLogProductPostDetailResponse(
                productPost.getId(),
                productPost.getTitle(),
                productPost.getContent(),
//                productPost.getStock(),
                productPost.getImage(),
                productPost.getCategory(),
                productPost.getProgressStatus(),
                productPost.getStartDate(),
                productPost.getEndDate()
        );
    }
}
