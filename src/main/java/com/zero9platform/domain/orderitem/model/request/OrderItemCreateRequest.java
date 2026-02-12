package com.zero9platform.domain.orderitem.model.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemCreateRequest {

    private Long optionId;

    @Min(1)
    private Integer orderQuantity;
}
