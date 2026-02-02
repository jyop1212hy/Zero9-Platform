package com.zero9platform.domain.clickLog.model.response;

import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductPostSearchResponse {

    private final Long productPostId;
    private final String productTitle;
    private final Long ProductPrice;
    private final String imageUrl;

    public static ProductPostSearchResponse from(ProductPost productPost) {
        return new ProductPostSearchResponse(
                productPost.getId(),
                productPost.getTitle(),
                productPost.getOriginalPrice(),
                productPost.getImage()
        );
    }
}
