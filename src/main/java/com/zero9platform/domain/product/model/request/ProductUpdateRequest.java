package com.zero9platform.domain.product.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductUpdateRequest {

    private String name;
    private String description;

    @Positive(message = "상품 가격은 0보다 커야 합니다")
    private Long productPrice;
}
