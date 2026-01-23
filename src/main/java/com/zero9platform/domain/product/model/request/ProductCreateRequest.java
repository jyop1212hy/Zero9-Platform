package com.zero9platform.domain.product.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCreateRequest {

    private String name;
    private String description;
    private Long productPrice;
}
