package com.zero9platform.common.enums;

import lombok.Getter;

@Getter
public enum RankingPeriod {

    REALTIME, // 실시간 (메인)
    DAILY,    // 하루
    WEEKLY,   // 1주
    MONTHLY,  // 1개월
}