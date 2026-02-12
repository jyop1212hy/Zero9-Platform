package com.zero9platform.common.enums;

import lombok.Getter;

@Getter
public enum GppProgressStatus {

    READY("준비중"),
    DOING("진행중"),
    END("종료됨");

    private final String description;

    GppProgressStatus(String description) {
        this.description = description;
    }

//    // 변환 메서드
//    public static GppProgressStatus from(String value) {
//        return Arrays.stream(values()) // values() : enum에 있는 모든 값들을 배열로 가져옴
//                .filter(c -> c.description.equals(value))
//                .findFirst()
//                .orElseThrow(() ->
//                        new CustomException(ExceptionCode.GPP_PROGRESS_STATUS_NOT_FOUND));
//    }
}