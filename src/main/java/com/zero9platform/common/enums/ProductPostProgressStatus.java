package com.zero9platform.common.enums;

import com.zero9platform.common.exception.CustomException;

import java.util.Arrays;

public enum ProductPostProgressStatus {

    READY("준비중"), DOING("진행중"), END("종료됨");

    private final String description;

    ProductPostProgressStatus(String description) {
        this.description = description;
    }

    public static ProductPostProgressStatus from(String value) {

        return Arrays.stream(values())
                .filter(pps -> pps.description.equals(value))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionCode.PP_PROGRESS_STATUS_NOT_FOUND));
    }
}
