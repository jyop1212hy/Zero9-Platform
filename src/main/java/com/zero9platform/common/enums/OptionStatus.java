package com.zero9platform.common.enums;

import lombok.Getter;

@Getter
public enum OptionStatus {

    ACTIVE("옵션 활성화"),
    INACTIVE("옵션 비활성화");

    private final String description;

    OptionStatus(String description) {
        this.description = description;
    }
}