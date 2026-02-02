package com.zero9platform.domain.grouppurchase_post.repository;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupPurchasePostRepository extends JpaRepository<GroupPurchasePost, Long> {

    // 공동구매 게시물 상세 조회 - [삭제처리 제외]
    Optional<GroupPurchasePost> findByIdAndDeletedAtIsNull(Long id);

    // 공동구매 게시물 페이징 조회 [삭제처리 제외]
    Page<GroupPurchasePost> findAllByDeletedAtIsNull(Pageable pageable);

    // 조회 수 증가 - [삭제처리 제외]
    // DB에서 직접 증가, 영속성 컨텍스트를 거치지 않음
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update GroupPurchasePost g
                set g.viewCount = g.viewCount + 1
                where g.id = :gppId and g.deletedAt is null
            """)
    int increaseViewCount(@Param("gppId") Long gppId);

    // 공동구매 게시물 모집상태 변경 대상(준비중->모집중 or 모집중->종료됨) 조회 [삭제처리 제외]
    @Query("""
    select g
    from GroupPurchasePost g
    where g.deletedAt is null
      and (
           (g.gppProgressStatus = 'READY' and g.startDate <= :now) or (g.gppProgressStatus = 'DOING' and g.endDate <= :now)
      )
""")
    List<GroupPurchasePost> findProgressStatusChangeTargets(@Param("now") LocalDateTime now);

//    // READY -> DOING
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query("""
//    update GroupPurchasePost g
//    set g.gppProgressStatus = 'DOING'
//    where g.deletedAt is null
//      and g.gppProgressStatus = 'READY'
//      and g.startDate <= :now
//""")
//    int updateReadyToDoing(@Param("now") LocalDateTime now);
//
//    // DOING -> END
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query("""
//    update GroupPurchasePost g
//    set g.gppProgressStatus = 'END'
//    where g.deletedAt is null
//      and g.gppProgressStatus = 'DOING'
//      and g.endDate <= :now
//""")
//    int updateDoingToEnd(@Param("now") LocalDateTime now);

    // ViewCount 랭킹 조회
    List<GroupPurchasePost> findTop10ByDeletedAtIsNullOrderByViewCountDesc();
}
