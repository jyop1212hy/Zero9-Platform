package com.zero9platform.common.enums;

import lombok.Getter;

@Getter
public enum StockStatus {

    IN_STOCK("재고 있음"), SOLD_OUT("재고 없음");

    private final String description;

    StockStatus(String description) {
        this.description = description;
    }

}
