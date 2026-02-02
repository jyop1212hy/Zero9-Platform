package com.zero9platform.domain.orderitem.model.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemCreateRequest {

    private Long optionId;

    @Min(1)
    private Integer orderQuantity;
}
