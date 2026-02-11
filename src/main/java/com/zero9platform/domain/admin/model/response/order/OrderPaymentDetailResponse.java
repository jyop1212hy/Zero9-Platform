package com.zero9platform.domain.admin.model.response.order;

import com.zero9platform.common.enums.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class OrderPaymentDetailResponse {

    private final Long orderId;
    private final String nickname;
    private final String productOptionName;
    private final Long totalAmount;
    private final String orderNo;
    private final String paymentKey;
    private final String orderStatus;
    private final String canceledReason;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
