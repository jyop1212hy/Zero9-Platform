package com.zero9platform.domain.orderitem.model.response;

import com.zero9platform.domain.orderitem.entity.OrderItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderItemCreateResponse {

    private final Long id;
    private final Long userId;
    private final String nickname;
    private final Long productPostId;
    private final String productTitle;
    private final String productPostStatus;
    private final Long optionId;
    private final String optionName;
    private final Long optionPrice;
    private final Integer capacity;

    public static OrderItemCreateResponse from(OrderItem orderItem) {

        return new OrderItemCreateResponse(
                orderItem.getId(),
                orderItem.getUser().getId(),
                orderItem.getUser().getNickname(),
                orderItem.getProductPost().getId(),
                orderItem.getProductPost().getTitle(),
                orderItem.getProductPost().getProductPostStatus(),
                orderItem.getProductPostOption().getId(),
                orderItem.getProductPostOption().getName(),
                orderItem.getProductPostOption().getOptionPrice(),
                orderItem.getProductPostOption().getCapacity()
        );
    }

}
