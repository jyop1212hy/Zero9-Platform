package com.zero9platform.domain.product.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품명을 입력해주세요")
    private String name;

    @NotBlank(message = "상품 설명을 입력해주세요")
    private String description;

    @NotNull(message = "상품 가격을 입력해주세요")
    @Positive(message = "상품 가격은 0보다 커야 합니다")
    private Long productPrice;
}
