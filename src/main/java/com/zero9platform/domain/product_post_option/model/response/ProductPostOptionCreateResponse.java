package com.zero9platform.domain.product_post_option.model.response;

import com.zero9platform.common.enums.StockStatus;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostOptionCreateResponse {

    private final Long id;
    private final String name;
    private final Long salePrice;
    private final Integer stockQuantity;
    private final String stockStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProductPostOptionCreateResponse from(ProductPostOption option) {

        StockStatus stockStatus = StockStatus.valueOf(option.getStockStatus());

        return new ProductPostOptionCreateResponse(
                option.getId(),
                option.getName(),
                option.getSalePrice(),
                option.getStockQuantity(),
                stockStatus.name(),
                option.getCreatedAt(),
                option.getUpdatedAt()
        );
    }
}