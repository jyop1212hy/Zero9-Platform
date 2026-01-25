package com.zero9platform.domain.product_post_favorite.model.response;

import com.zero9platform.domain.product_post_favorite.entity.ProductPostFavorite;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductPostFavoriteCreateResponse {

    private final Long userId;
    private final String nickname;
    private final Long productPostId;

    public static ProductPostFavoriteCreateResponse from(ProductPostFavorite productPostFavorite) {
        return new ProductPostFavoriteCreateResponse(
                productPostFavorite.getUser().getId(),
                productPostFavorite.getUser().getNickname(),
                productPostFavorite.getProductPost().getId()
        );
    }
}
