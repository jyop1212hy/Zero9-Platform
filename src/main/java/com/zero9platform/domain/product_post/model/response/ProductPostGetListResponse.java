package com.zero9platform.domain.product_post.model.response;

import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProductPostGetListResponse {

    private final Long id;
    private final String title;
    private final String name;
    private final Long originalPrice;
    private final String image;
    private final Long favoriteCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostGetListResponse from(ProductPost productPost, String image, Long favoriteCount) {

        return new ProductPostGetListResponse(
                productPost.getId(),
                productPost.getTitle(),
                productPost.getName(),
                productPost.getOriginalPrice(),
                image,
                favoriteCount,
                productPost.getCreatedAt(),
                productPost.getUpdatedAt()
        );
    }
}