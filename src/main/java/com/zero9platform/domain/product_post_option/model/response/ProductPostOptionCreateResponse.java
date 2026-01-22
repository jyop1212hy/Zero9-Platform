package com.zero9platform.domain.product_post_option.model.response;

import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostOptionCreateResponse {

    private final Long id;
    private final String name;
    private final Long price;
    private final Integer capacity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostOptionCreateResponse from(ProductPostOption option) {
        return new ProductPostOptionCreateResponse(
                option.getId(),
                option.getName(),
                option.getPrice(),
                option.getCapacity(),
                option.getCreatedAt(),
                option.getUpdatedAt()
        );
    }
}