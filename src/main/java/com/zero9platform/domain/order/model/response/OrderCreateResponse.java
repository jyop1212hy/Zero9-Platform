package com.zero9platform.domain.order.model.response;

import com.zero9platform.common.enums.OrderStatus;
import com.zero9platform.domain.order.entity.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderCreateResponse {

    private final Long id;
    private final Long userId;
    private final String nickname;
    private final String email;
    private final String phone;
    private final String orderNo;
    private final Long totalAmount;
    private final String orderStatus;
    private final Long orderItemId;
    private final String optionName;

    public static OrderCreateResponse from(Order order) {

        OrderStatus orderStatus = OrderStatus.valueOf(order.getOrderStatus());

        return new OrderCreateResponse(
                order.getId(),
                order.getOrderItem().getUser().getId(),
                order.getOrderItem().getUser().getNickname(),
                order.getOrderItem().getUser().getEmail(),
                order.getOrderItem().getUser().getPhone(),
                order.getOrderNo(),
                order.getTotalAmount(),
                orderStatus.getDescription(),
                order.getOrderItem().getId(),
                order.getOrderItem().getProductPostOption().getName()
        );
    }
}