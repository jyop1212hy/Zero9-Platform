package com.zero9platform.domain.product_post.model.response;

import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductPostGetDetailResponse {

    private final Long id;
    private final String category;
    private final String progressStatus;
    private final String title;
    private final String name;
    private final String content;
    private final Long originalPrice;
    private final Long favoriteCount;
    private final List<ProductPostOptionCreateResponse> optionList;
    private final String image;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostGetDetailResponse from(ProductPost productPost, String image, Long favoriteCount) {

        List<ProductPostOptionCreateResponse> optionList = productPost.getProductPostOptionList().stream()
                .map(ProductPostOptionCreateResponse::from)
                .toList();

        return new ProductPostGetDetailResponse(
                productPost.getId(),
                productPost.getCategory(),
                productPost.getProgressStatus(),
                productPost.getTitle(),
                productPost.getName(),
                productPost.getContent(),
                productPost.getOriginalPrice(),
                favoriteCount,
                optionList,
                image,
                productPost.getStartDate(),
                productPost.getEndDate(),
                productPost.getCreatedAt(),
                productPost.getUpdatedAt()
        );
    }
}