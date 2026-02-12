package com.zero9platform.domain.product_post_favorite.repository;

import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_favorite.entity.ProductPostFavorite;
import com.zero9platform.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductPostFavoriteRepository extends JpaRepository<ProductPostFavorite, Long> {

    // 찜 등록 여부 확인용
    Optional<ProductPostFavorite> findByUserAndProductPost(User user, ProductPost productPost);

    // 찜 등록 중복 방지용
    boolean existsByUser_IdAndProductPost_Id(Long userId, Long productPostId);

    // 본인 찜 등록 리스트 조회
    Page<ProductPostFavorite> findByUser_Id(Long id, Pageable pageable);

    // // 공동구매 게시물들의 찜 개수 조회 (Count 쿼리 호출) <- 이부분은 고도화/리펙터링 대상임
    // 넘겨받은 gppId들을 대상으로 -> COUNT(gf)를 해서 row수를 셈
    // 각 gppId에 대응하는 GppFavorite의 행 수를 계산
    @Query("""
            SELECT gf.productPost.id, COUNT(gf.id)
            FROM ProductPostFavorite gf
            WHERE gf.productPost.id IN :gppIdList
            GROUP BY gf.productPost.id
    """)
    List<Object[]> countByGppIdList(@Param("gppIdList") List<Long> gppIdList);

    // 내부적으로는 아래와 같음
    //    SELECT gpp_id, COUNT(*)
    //    FROM gpp_favorites
    //    WHERE gpp_id IN (1, 2, 3, ...)
    //    GROUP BY gpp_id;

    // 현재 게시물의 찜 개수 확인용
    Long countByProductPost_Id(Long productPostId);

    // 특정 유저가 찜한 상품들의 id 조회
    @Query("SELECT pf.productPost.id FROM ProductPostFavorite pf WHERE pf.user.id = :userId")
    List<Long> findProductPostIdsByUserId(@Param("userId") Long userId);

    // 기간별 찜 집계 쿼리
//    @Query("""
//    SELECT new com.zero9platform.domain.ranking.model.response
//        .ProductPostFavoriteRankingAggregateResponse(
//            p.id,
//            p.title,
//            COUNT(f.id)
//        )
//    FROM ProductPostFavorite f
//    JOIN f.productPost p
//    WHERE f.createdAt BETWEEN :from AND :to
//      AND p.progressStatus = :status
//    GROUP BY p.id, p.title
//    ORDER BY COUNT(f.id) DESC
//""")
//    List<ProductPostFavoriteRankingAggregateResponse> findTopByFavoriteBetween(
//            @Param("from") LocalDateTime from,
//            @Param("to") LocalDateTime to,
//            @Param("status") String status,
//            Pageable pageable
//    );
}