package com.zero9platform.domain.product_post.model.response;

import com.zero9platform.domain.product.entity.Product;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import com.zero9platform.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductPostCreateResponse {

    private final Long id;
    private final Long userId;
    private final Long productId;
    private final String title;
    private final String content;
    private final Long productPrice;
    private final Long stock;
    private final List<ProductPostOptionCreateResponse> optionList;
    private final String image;
    private final String category;
    private final String productPostProgressStatus;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostCreateResponse from(User user, Product product, ProductPost productPost) {

        List<ProductPostOptionCreateResponse> optionList = productPost.getProductPostOptionList().stream()
                .map(ProductPostOptionCreateResponse::from)
                .toList();

        return new ProductPostCreateResponse(
                productPost.getId(),
                user.getId(),
                product.getId(),
                productPost.getTitle(),
                productPost.getContent(),
                product.getProductPrice(),
                productPost.getStock(),
                optionList,
                productPost.getImage(),
                productPost.getCategory(),
                productPost.getProductPostProgressStatus(),
                productPost.getStartDate(),
                productPost.getEndDate(),
                productPost.getCreatedAt(),
                productPost.getUpdatedAt()
        );
    }
}
