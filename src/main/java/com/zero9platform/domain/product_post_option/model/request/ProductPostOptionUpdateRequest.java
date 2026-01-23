package com.zero9platform.domain.product_post_option.model.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductPostOptionUpdateRequest {

    private String name;

    @Min(0)
    private Long optionPrice;

    @Min(0)
    private Integer capacity;
}