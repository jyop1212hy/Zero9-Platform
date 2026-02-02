package com.zero9platform.domain.product_post_option.model.request;

import jakarta.validation.constraints.Min;
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
    private Long salePrice;

    @NotNull(message = "재고는 필수입니다.")
    @Min(value = 1, message = "재고는 최소 1개 이상이어야 합니다.")
    private Integer stockQuantity;
}