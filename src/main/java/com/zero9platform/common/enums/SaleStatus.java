package com.zero9platform.common.enums;

import lombok.Getter;

@Getter
public enum SaleStatus {

    ACTIVE("판매 및 조회 가능"),
    INACTIVE("판매 불가"),  // 옵션 < 0일 때
    SOLD_OUT("품절"); // 재고 <= 0일 때

    private final String description;

    SaleStatus(String description) {
        this.description = description;
    }
}
