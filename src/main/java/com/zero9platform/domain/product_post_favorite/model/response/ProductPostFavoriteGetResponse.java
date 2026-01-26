package com.zero9platform.domain.product_post_favorite.model.response;

import com.zero9platform.domain.product_post_favorite.entity.ProductPostFavorite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductPostFavoriteGetResponse {

    private final Long productPostId;
    private final Long userId;
    private final String productName;

    public static ProductPostFavoriteGetResponse from(ProductPostFavorite productPostFavorite) {
        return new ProductPostFavoriteGetResponse(
                productPostFavorite.getProductPost().getId(),
                productPostFavorite.getUser().getId(),
                productPostFavorite.getProductPost().getProduct().getName()
        );
    }
}