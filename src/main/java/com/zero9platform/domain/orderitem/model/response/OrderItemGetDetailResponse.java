package com.zero9platform.domain.orderitem.model.response;

import com.zero9platform.domain.orderitem.entity.OrderItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderItemGetDetailResponse {

    private final Long id;
    private final Long optionId;
    private final String optionName;
    private final Long optionPrice;
    private final Integer capacity;

    public static OrderItemGetDetailResponse from(OrderItem orderItem) {

        return new OrderItemGetDetailResponse(
                orderItem.getId(),
                orderItem.getProductPostOption().getId(),
                orderItem.getProductPostOption().getName(),
                orderItem.getProductPostOption().getSalePrice(),
                orderItem.getOrderQuantity()
        );
    }

}
