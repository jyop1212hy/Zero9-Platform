package com.zero9platform.domain.orderitem.model.response;

import com.zero9platform.domain.orderitem.entity.OrderItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderItemCreateResponse {

    private final Long id;
    private final Long optionId;
    private final String optionName;
    private final Long salePrice;
    private final Integer orderQuantity;

    public static OrderItemCreateResponse from(OrderItem orderItem) {

        return new OrderItemCreateResponse(
                orderItem.getId(),
                orderItem.getProductPostOption().getId(),
                orderItem.getProductPostOption().getName(),
                orderItem.getProductPostOption().getSalePrice(),
                orderItem.getOrderQuantity()
        );
    }
}