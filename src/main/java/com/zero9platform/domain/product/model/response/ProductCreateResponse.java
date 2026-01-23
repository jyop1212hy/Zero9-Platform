package com.zero9platform.domain.product.model.response;

import com.zero9platform.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCreateResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Long productPrice;

    public static ProductCreateResponse from(Product product) {

        return new ProductCreateResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getProductPrice()
        );
    }
}
