package com.zero9platform.domain.product_post_option.model.response;

import com.zero9platform.common.enums.OptionStatus;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostOptionDeleteResponse {

    private Long id;
    private Long productPostId;
    private String name;
    private Long optionPrice;
    private Integer capacity;
    private String optionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductPostOptionDeleteResponse from(ProductPostOption option) {

        OptionStatus optionStatus = OptionStatus.valueOf(option.getOptionStatus());

        return new ProductPostOptionDeleteResponse(
                option.getId(),
                option.getProductPost().getId(),
                option.getName(),
                option.getOptionPrice(),
                option.getCapacity(),
                optionStatus.name(),
                option.getCreatedAt(),
                option.getUpdatedAt()
        );
    }
}
