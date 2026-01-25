package com.zero9platform.common.enums;

import com.zero9platform.common.exception.CustomException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {

    PENDING("결제 대기"), PAID("결제 완료"), CANCELED("결제 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

//    public static OrderStatus from(String value) {
//
//        return Arrays.stream(values())
//                .filter(o -> o.description.equals(value))
//                .findFirst()
//                .orElseThrow(() -> new CustomException(ExceptionCode.ORDER_STATUS_NOT_FOUND));
//    }
}
