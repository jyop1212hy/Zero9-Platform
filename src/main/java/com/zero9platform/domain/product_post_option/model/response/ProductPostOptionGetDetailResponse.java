package com.zero9platform.domain.product_post_option.model.response;

import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostOptionGetDetailResponse {

    private final Long id;
    private final Long productPostId;
    private final String name;
    private final Long optionPrice;
    private final Integer capacity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostOptionGetDetailResponse from(ProductPostOption option) {
        return new ProductPostOptionGetDetailResponse(
                option.getId(),
                option.getProductPost().getId(),
                option.getName(),
                option.getOptionPrice(),
                option.getCapacity(),
                option.getCreatedAt(),
                option.getUpdatedAt()
        );
    }
}