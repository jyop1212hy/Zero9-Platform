package com.zero9platform.domain.product_post.model.response;

import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductPostGetListResponse {

    private final Long id;
    private final String title;
    private final String name;
    private final Long originalPrice;
    private final String image;
    private final String progressStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostGetListResponse from(ProductPost productPost) {

        List<ProductPostOptionCreateResponse> optionList = productPost.getProductPostOptionList().stream()
                .map(ProductPostOptionCreateResponse::from)
                .toList();

        return new ProductPostGetListResponse(
                productPost.getId(),
                productPost.getTitle(),
                productPost.getName(),
                productPost.getOriginalPrice(),
                productPost.getImage(),
                productPost.getProgressStatus(),
                productPost.getCreatedAt(),
                productPost.getUpdatedAt()
        );
    }
}
