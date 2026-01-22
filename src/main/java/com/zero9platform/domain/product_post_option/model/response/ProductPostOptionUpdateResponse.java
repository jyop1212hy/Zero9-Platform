package com.zero9platform.domain.product_post_option.model.response;

import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostOptionUpdateResponse {

    private Long id;
    private Long productPostId;
    private String name;
    private Long price;
    private Integer capacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductPostOptionUpdateResponse from(ProductPostOption option) {
        return new ProductPostOptionUpdateResponse(
                option.getId(),
                option.getProductPost().getId(),
                option.getName(),
                option.getPrice(),
                option.getCapacity(),
                option.getCreatedAt(),
                option.getUpdatedAt()
        );
    }
}