package com.zero9platform.domain.order.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentRequest {

    private String orderNo;
    private String paymentKey;
    private int amount;
}
