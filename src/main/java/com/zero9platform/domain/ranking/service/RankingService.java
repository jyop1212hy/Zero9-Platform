package com.zero9platform.domain.ranking.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.ranking.model.response.ProductPostFavoriteRankingListResponse;
import com.zero9platform.domain.ranking.model.response.SearchLogRankingListResponse;
import com.zero9platform.domain.ranking.repository.FavoriteRankingSnapshotRepository;
import com.zero9platform.domain.ranking.repository.KeywordRankingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Log4j2
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingCounter rankingCounter;
    private final ProductPostRepository productPostRepository;
    private final FavoriteRankingSnapshotRepository favoriteSnapshotRepository;
    private final KeywordRankingSnapshotRepository keywordRankingSnapshotRepository;
//    private final GroupPurchasePostRepository groupPurchasePostRepository;
//    private final SearchLogRepository searchLogRepository;

    /**
     * 인기 검색어 랭킹 조회
     */
    @Transactional(readOnly = true)
    public List<SearchLogRankingListResponse> searchKeywordRanking(RankingPeriod period) {

        // period 기간 검증
        periodValid(period);

        //npe 검증
        RankingPeriod resolved = (period == null) ? RankingPeriod.REALTIME : period;

        AtomicInteger rank = new AtomicInteger(1);
        List<SearchLogRankingListResponse> search = List.of();

        // REALTIME/DAILY/WEEKLY/MONTHLY 모두 Redis 우선 조회
        if (resolved == RankingPeriod.REALTIME || resolved == RankingPeriod.DAILY || resolved == RankingPeriod.WEEKLY || resolved == RankingPeriod.MONTHLY) {
            search = rankingCounter.topRankings("SEARCH", resolved, 10).stream()
                    .map(tuple -> SearchLogRankingListResponse.of(
                            rank.getAndIncrement(),
                            tuple.getValue(),
                            tuple.getScore().longValue()))
                    .toList();
        }

        // [추가 위치] Redis가 비어있는데 요청이 REALTIME이었다면, DB 조회를 위해 DAILY로 전환
        if (search.isEmpty() && resolved == RankingPeriod.REALTIME) {
            resolved = RankingPeriod.DAILY;
            rank.set(1); // 순위 숫자를 다시 1부터 시작하도록 리셋
        }

        // Redis가 비었으면(=스냅샷 전 단계) DB 스냅샷으로 fallback
        if (search.isEmpty()) {
            LocalDateTime targetTime = getTargetTime(resolved);
            String targetDate = rankingCounter.dateRedisKey(resolved, targetTime);
            return keywordRankingSnapshotRepository.findByPeriodAndTargetDateOrderByKeywordCountDesc(resolved, targetDate, PageRequest.of(0, 10))
                    .stream()
                    .map(s -> SearchLogRankingListResponse.of(
                            rank.getAndIncrement(),
                            s.getKeyword(),
                            s.getKeywordCount()))
                    .toList();
        }
        return search;
    }

    /**
     * 공동구매 상품 게시물 랭킹 조회(찜)
     */
    @Transactional(readOnly = true)
    public List<ProductPostFavoriteRankingListResponse> favoriteRanking(RankingPeriod period) {

        // period 기간 검증
        periodValid(period);

        //npe 검증
        RankingPeriod resolved = (period == null) ? RankingPeriod.REALTIME : period;

        AtomicInteger rank = new AtomicInteger(1);
        List<ProductPostFavoriteRankingListResponse> favorite = List.of();

        // REALTIME → 캐시
        if (resolved == RankingPeriod.REALTIME || resolved == RankingPeriod.DAILY || resolved == RankingPeriod.WEEKLY || resolved == RankingPeriod.MONTHLY) {
            favorite = rankingCounter.topRankings("FAVORITE", resolved, 10).stream()
                    .map(tuple -> {
                        Long productId = Long.parseLong(tuple.getValue());
                        // 게시물이 삭제되었어도 랭킹 리스트는 유지되도록 예외 대신 null 처리 후 분기
                        String title = productPostRepository.findById(productId)
                                .map(ProductPost::getTitle)
                                .orElse("삭제된 게시물");

                        return ProductPostFavoriteRankingListResponse.of(
                                rank.getAndIncrement(),
                                productId,
                                title,
                                tuple.getScore().longValue());
                    })
                    .toList();
        }

        // [추가 위치] Redis가 비어있는데 요청이 REALTIME이었다면, DB 조회를 위해 DAILY로 전환
        if (favorite.isEmpty() && resolved == RankingPeriod.REALTIME) {
            resolved = RankingPeriod.DAILY;
            rank.set(1); // 순위 리셋
        }

        // DAILY / WEEKLY / MONTHLY → DB
        if (favorite.isEmpty()) {
            LocalDateTime targetTime = getTargetTime(resolved);
            String targetDate = rankingCounter.dateRedisKey(resolved, targetTime);
            return favoriteSnapshotRepository.findByPeriodAndTargetDateOrderByFavoriteCountDesc(resolved, targetDate, PageRequest.of(0, 10))
                    .stream()
                    .map(s -> {
                        // 게시물이 삭제되었어도 랭킹 리스트는 유지되도록 예외 대신 null 처리 후 분기
                        String title = productPostRepository.findById(s.getProductPostId())
                                .map(ProductPost::getTitle)
                                .orElse("삭제된 게시물");

                        return ProductPostFavoriteRankingListResponse.of(
                                rank.getAndIncrement(),
                                s.getProductPostId(),
                                title,
                                s.getFavoriteCount());
                    })
                    .toList();
        }
        return favorite;
    }

    /**
     * 랭킹 조회시 허용된 기간만 처리
     */
    private void periodValid(RankingPeriod period) {
        if (period != null && period != RankingPeriod.REALTIME &&
                period != RankingPeriod.DAILY &&
                period != RankingPeriod.WEEKLY &&
                period != RankingPeriod.MONTHLY) {
            throw new CustomException(ExceptionCode.INVALID_PERIOD);
        }
    }

    /**
     * DB 조회 시 필요한 시점 계산 (어제, 지난주, 지난달)
     */
    private LocalDateTime getTargetTime(RankingPeriod period) {
        return switch (period) {
            case DAILY -> LocalDateTime.now().minusDays(1);
            case WEEKLY -> LocalDateTime.now().minusWeeks(1);
            case MONTHLY -> LocalDateTime.now().minusMonths(1);
            default -> LocalDateTime.now().minusDays(1);
        };
    }

//    /**
//     * 공동구매 조회수 랭킹
//     */
//    @Transactional(readOnly = true)
//    public List<GroupPurchasePostRankingListResponse> groupPurchasePostRanking(RankingPeriod period) {
//        RankingPeriod resolved = resolvePeriod(period);
//        Map<String, Long> counters = rankingCounter.getAll(resolved);
//        AtomicInteger rank = new AtomicInteger(1);
//
//        return counters.entrySet().stream()
//                .filter(e -> e.getKey().startsWith(viewPrefix(resolved)))
//                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
//                .limit(10)
//                .map(e -> {
//                    Long gppId = extractLastId(e.getKey());
//                    GroupPurchasePost gpp = groupPurchasePostRepository
//                            .findByIdAndDeletedAtIsNull(gppId)
//                            .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));
//
//                    return GroupPurchasePostRankingListResponse.of(
//                            rank.getAndIncrement(),
//                            gpp.getId(),
//                            gpp.getProductName(),
//                            e.getValue()
//                    );
//                })
//                .toList();
//    }
//
//    /**
//     * 기간별 상품 찜 랭킹 (관리자용)
//     * period: DAILY / WEEKLY / MONTHLY
//     */
//    @Transactional(readOnly = true)
//    public List<ProductPostFavoriteRankingListResponse> loadPeriodFavoriteRanking(AuthUser authUser, RankingPeriod period, LocalDate from, LocalDate to) {
//
//        // 관리자 권한 검증
//        if (!"ADMIN".equals(authUser.getUserRole().name())) {throw new CustomException(ExceptionCode.NO_PERMISSION);}
//
//        // period 공백 검증
//        if (period == null || period.name().isBlank()) {period = RankingPeriod.REALTIME;}
//
//        // 기간 랭킹 허용 값 검증
//        if (period != RankingPeriod.REALTIME && period != RankingPeriod.DAILY && period != RankingPeriod.WEEKLY && period != RankingPeriod.MONTHLY) {throw new CustomException(ExceptionCode.INVALID_PERIOD);}
//
//        // 날짜 검증
//        if (from != null && to != null && from.isAfter(to)) {throw new CustomException(ExceptionCode.INVALID_DATE_RANGE);}
//
//        // period 공백 검증
//        LocalDateTime[] range = RankingPeriodPolicy.resolve(period, from, to);
//
//        // 기간 내 찜 기준 랭킹 집계
//        List<ProductPostFavoriteRankingAggregateResponse> favorite = productPostFavoriteRepository.findTopByFavoriteBetween(range[0], range[1], ProgressStatus.DOING.name(), PageRequest.of(0, 10));
//
//        AtomicInteger rank = new AtomicInteger(1);
//        List<ProductPostFavoriteRankingListResponse> result = favorite.stream()
//                .map(a ->
//                        ProductPostFavoriteRankingListResponse.from(
//                                rank.getAndIncrement(),
//                                a.getProductPostId(),
//                                a.getTitle(),
//                                a.getFavoriteCount()
//                        ))
//                .toList();
//
//        return result;
//    }
}
