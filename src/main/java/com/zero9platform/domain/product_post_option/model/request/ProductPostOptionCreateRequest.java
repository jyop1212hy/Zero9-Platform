package com.zero9platform.domain.product_post_option.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductPostOptionCreateRequest {

    @NotBlank(message = "옵션명은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    private Long optionPrice;

    @NotNull(message = "수량은 필수입니다.")
    private Integer capacity;
}