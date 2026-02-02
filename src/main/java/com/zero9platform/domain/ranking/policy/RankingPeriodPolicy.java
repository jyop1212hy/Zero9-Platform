//package com.zero9platform.domain.ranking.policy;
//
//import com.zero9platform.common.enums.ExceptionCode;
//import com.zero9platform.common.enums.RankingPeriod;
//import com.zero9platform.common.exception.CustomException;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//public class RankingPeriodPolicy {
//
//    private RankingPeriodPolicy() {}
//
//    public static LocalDateTime[] resolve(RankingPeriod period, LocalDate from, LocalDate to) {
//
//         // 허용 값 검증
//        if (period != RankingPeriod.REALTIME
//                && period != RankingPeriod.DAILY
//                && period != RankingPeriod.WEEKLY
//                && period != RankingPeriod.MONTHLY) {
//            throw new CustomException(ExceptionCode.INVALID_PERIOD);
//        }
//
//        // 관리자 커스텀 기간
//        if (from != null && to != null) {
//            return new LocalDateTime[]{
//                    from.atStartOfDay(),
//                    to.atTime(23, 59, 59)
//            };
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//
//        return switch (period) {
//            case REALTIME -> new LocalDateTime[]{
//                    now.minusMinutes(5), // 또는 누적
//                    now
//            };
//            case DAILY -> new LocalDateTime[]{
//                    now.toLocalDate().atStartOfDay(),
//                    now
//            };
//            case WEEKLY -> new LocalDateTime[]{
//                    now.minusWeeks(1),
//                    now
//            };
//            case MONTHLY -> new LocalDateTime[]{
//                    now.minusMonths(1),
//                    now
//            };
//        };
//    }
//}
