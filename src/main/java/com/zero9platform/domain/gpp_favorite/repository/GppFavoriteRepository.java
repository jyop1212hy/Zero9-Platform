package com.zero9platform.domain.gpp_favorite.repository;

import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GppFavoriteRepository extends JpaRepository<GppFavorite, Long> {

    // 찜 등록 확인용
    Optional<GppFavorite> findByUserIdAndGroupPurchasePostId(Long userId, Long gppId);

    // 찜 등록 중복 방지용
    boolean existsByUserIdAndGroupPurchasePostId(Long userId, Long gppId);

    // 본인 낌리스트 조회용(삭제 된 게시물은 제외)
    @Query("""
                SELECT f
                FROM GppFavorite f
                JOIN FETCH f.groupPurchasePost gp
                WHERE f.user.id = :userId
                  AND gp.deletedAt IS NULL
            """)
    Page<GppFavorite> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // // 공동구매 게시물들의 찜 개수 조회 (Count 쿼리 호출) <- 이부분은 고도화/리펙터링 대상임
    // 넘겨받은 gppId들을 대상으로 -> COUNT(gf)를 해서 row수를 셈
    // 각 gppId에 대응하는 GppFavorite의 행 수를 계산
    @Query("""
            SELECT gf.groupPurchasePost.id, COUNT(gf)
            FROM GppFavorite gf
            WHERE gf.groupPurchasePost.id IN :gppIdList
            GROUP BY gf.groupPurchasePost.id
            """)
    List<Object[]> countByGppIdList(@Param("gppIdList") List<Long> gppIdList);
    // 내부적으로는 아래와 같음
    //    SELECT gpp_id, COUNT(*)
    //    FROM gpp_favorites
    //    WHERE gpp_id IN (1, 2, 3, ...)
    //    GROUP BY gpp_id;
}