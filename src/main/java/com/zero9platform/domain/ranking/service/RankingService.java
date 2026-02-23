package com.zero9platform.domain.ranking.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.RankingPeriod;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.ranking.model.response.*;
import com.zero9platform.domain.ranking.repository.FavoriteRankingSnapshotRepository;
import com.zero9platform.domain.ranking.repository.KeywordRankingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingCounter rankingCounter;
    private final ProductPostRepository productPostRepository;
    private final FavoriteRankingSnapshotRepository favoriteSnapshotRepository;
    private final KeywordRankingSnapshotRepository keywordRankingSnapshotRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final StringRedisTemplate redisTemplate;

    // gpp 랭킹용 상수값 선언
    private static final String GPP_TOTAL_RANKING_KEY = "gpp:ranking:total";
    private static final String GPP_DAILY_RANKING_KEY_PREFIX = "gpp:ranking:daily:";
    private static final int RANKING_LIMIT = 10;
    private static final String GPP_WEEKLY_RANKING_KEY_PREFIX = "gpp:ranking:weekly:";
    private static final int WEEK_DAYS = 7;

    /**
     * 공동구매 게시물 전체 누적 랭킹 (Redis)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostTotalRankingResponse> groupPurchasePostTotalRanking() {

        // Redis ZSet에서 상위 N개 조회
        Set<ZSetOperations.TypedTuple<String>> rankingSet =
                redisTemplate.opsForZSet().reverseRangeWithScores(GPP_TOTAL_RANKING_KEY, 0, RANKING_LIMIT - 1);

        // Redis 데이터가 없으면 빈 리스트 반환
        if (rankingSet == null || rankingSet.isEmpty()) {
            return List.of();
        }

        // Redis에 있는 게시물 ID만 추출
        List<Long> gppIds = rankingSet.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();

        // 삭제되지 않은 게시물만 조회
        List<GroupPurchasePost> gppLists =
                groupPurchasePostRepository.findAllByIdInAndDeletedAtIsNull(gppIds);

        // ID -> Entity 매핑 (O(1) 조회용)
        Map<Long, GroupPurchasePost> gppMap =
                gppLists.stream()
                        .collect(Collectors.toMap(GroupPurchasePost::getId, p -> p));

        // Redis 순서 유지하면서 DTO 생성
        List<GroupPurchasePostTotalRankingResponse> result = new ArrayList<>();

        // 랭킹 카운터용
        // 메서드 내부에서 멀티스레드처리(카운터 공유) 안함, atomicInteger 안써도 된다고 판단
        int rank = 1;

        // Redis ZSet에서 가져온 데이터를 하나씩 꺼내는 반복문
        // tuple 내부에 [ value:gppId(String), score:조회수(Double) ]
        for (ZSetOperations.TypedTuple<String> tuple : rankingSet) {

            // null 방어 코드 - null이면 건너뜀
            if (tuple.getValue() == null || tuple.getScore() == null) {
                continue;
            }

            // Redis ZSet의 튜플안에 String으로 저장했기 때문에 Long으로 변환
            Long gppId = Long.valueOf(tuple.getValue());
            // Map에서 id로 엔티티 가져옴
            GroupPurchasePost gpp = gppMap.get(gppId);

            // 삭제된 게시물이거나 DB에 없는 경우 스킵
            if (gpp == null) {
                continue;
            }

            result.add(
                    GroupPurchasePostTotalRankingResponse.from(
                            rank++,
                            gpp.getId(),
                            gpp.getProductName(),
                            tuple.getScore().longValue()
                    )
            );
        }

        return result;
    }

    /**
     * 공동구매 게시물 오늘의 실시간 랭킹 (Redis)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostTodayRankingResponse> groupPurchasePostTodayRanking() {

        // 일일 키값
        String todayKey = GPP_DAILY_RANKING_KEY_PREFIX + LocalDate.now();

        // Redis ZSet 조회
        Set<ZSetOperations.TypedTuple<String>> rankingSet =
                redisTemplate.opsForZSet()
                        .reverseRangeWithScores(todayKey, 0, RANKING_LIMIT - 1);

        if (rankingSet == null || rankingSet.isEmpty()) {
            return List.of();
        }

        // ID 추출
        List<Long> gppIds = rankingSet.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();

        // 삭제되지 않은 게시물 조회
        List<GroupPurchasePost> gppLists =
                groupPurchasePostRepository.findAllByIdInAndDeletedAtIsNull(gppIds);

        // Map 변환 (O(1) 조회)
        Map<Long, GroupPurchasePost> gppMap =
                gppLists.stream()
                        .collect(Collectors.toMap(GroupPurchasePost::getId, p -> p));

        // DTO 생성
        List<GroupPurchasePostTodayRankingResponse> result = new ArrayList<>();

        // 랭킹 카운터용
        // 메서드 내부에서 멀티스레드처리(카운터 공유) 안함, atomicInteger 안써도 된다고 판단
        int rank = 1;

        // Redis ZSet에서 가져온 데이터를 하나씩 꺼내는 반복문
        // tuple 내부에 [ value:gppId(String), score:조회수(Double) ]
        for (ZSetOperations.TypedTuple<String> tuple : rankingSet) {

            // null 방어 코드 - null이면 건너뜀
            if (tuple.getValue() == null || tuple.getScore() == null) {
                continue;
            }

            // Redis ZSet의 튜플안에 String으로 저장했기 때문에 Long으로 변환
            Long gppId = Long.valueOf(tuple.getValue());
            // Map에서 id로 엔티티 가져옴
            GroupPurchasePost gpp = gppMap.get(gppId);

            // 삭제된 게시물이거나 DB에 없는 경우 스킵
            if (gpp == null) {
                continue;
            }

            result.add(
                    GroupPurchasePostTodayRankingResponse.from(
                            rank++,
                            gpp.getId(),
                            gpp.getProductName(),
                            tuple.getScore().longValue()
                    )
            );
        }

        return result;
    }

    /**
     * 공동구매 게시물 주간 랭킹 TOP10 (ZUNIONSTORE 기반)
     */
    @Transactional(readOnly = true)
    public List<GroupPurchasePostTodayRankingResponse> groupPurchasePostWeeklyRanking() {

        // 최근 7일 key 생성
        List<String> last7DaysKeys = new ArrayList<>();

        for (int i = 0; i < WEEK_DAYS; i++) {
            String key = GPP_DAILY_RANKING_KEY_PREFIX + LocalDate.now().minusDays(i);
            last7DaysKeys.add(key);
        }

        // 주간 랭킹 저장용 key
        String weeklyKey = GPP_WEEKLY_RANKING_KEY_PREFIX + LocalDate.now();

        // ZUNIONSTORE 실행
        // 최근 7일 daily ZSet을 합산하여 weeklyKey에 저장
        redisTemplate.opsForZSet().unionAndStore(
                last7DaysKeys.get(0),
                last7DaysKeys.subList(1, last7DaysKeys.size()),
                weeklyKey
        );

        // TTL 설정할 것

        // 상위 10개 조회
        Set<ZSetOperations.TypedTuple<String>> rankingSet =
                redisTemplate.opsForZSet().reverseRangeWithScores(weeklyKey, 0, RANKING_LIMIT - 1);

        if (rankingSet == null || rankingSet.isEmpty()) {
            return List.of();
        }

        // TOP10 ZSet(rankingSet)에서 gppId 추출
        List<Long> gppIds = rankingSet.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();

        // 리스트에 담긴 gppId들에 대응되는 객체리스트 조회
        List<GroupPurchasePost> gppLists = groupPurchasePostRepository.findAllByIdInAndDeletedAtIsNull(gppIds);

        // 조회한 객체 리스트의 gppId들을 매핑
        Map<Long, GroupPurchasePost> gppMap =
                gppLists.stream()
                        .collect(Collectors.toMap(GroupPurchasePost::getId, p -> p));

        // 반환용 리스트 선언
        List<GroupPurchasePostTodayRankingResponse> result = new ArrayList<>();

        // 순위 카운터
        int rank = 1;

        // 순위 정렬 + DTO 변환
        for (ZSetOperations.TypedTuple<String> tuple : rankingSet) {

            if (tuple.getValue() == null || tuple.getScore() == null) continue;

            Long gppId = Long.valueOf(tuple.getValue());
            GroupPurchasePost post = gppMap.get(gppId);

            if (post == null) continue;

            result.add(
                    GroupPurchasePostTodayRankingResponse.from(
                            rank++,
                            post.getId(),
                            post.getProductName(),
                            tuple.getScore().longValue()
                    )
            );
        }

        return result;
    }

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
            throw new CustomException(ExceptionCode.RANKING_INVALID_PERIOD);
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
}
