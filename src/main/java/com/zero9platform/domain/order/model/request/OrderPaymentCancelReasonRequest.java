package com.zero9platform.domain.order.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentCancelReasonRequest {

    private String canceledReason;
}
