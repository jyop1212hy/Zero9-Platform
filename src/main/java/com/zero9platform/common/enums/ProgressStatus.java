package com.zero9platform.common.enums;

public enum ProgressStatus {

    READY("준비중"),
    DOING("진행중"),
    END("종료됨");

    private final String description;

    ProgressStatus(String description) {
        this.description = description;
    }
}