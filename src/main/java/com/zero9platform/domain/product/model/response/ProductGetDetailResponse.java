package com.zero9platform.domain.product.model.response;

import com.zero9platform.domain.product.entity.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProductGetDetailResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Long productPrice;

    public static ProductGetDetailResponse from(Product product) {

        return new ProductGetDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getProductPrice()
        );
    }
}
