package com.zero9platform.domain.grouppurchase_post.repository;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    // 조회수 일괄 증가 - [삭제처리 제외]
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update GroupPurchasePost g
                set g.viewCount = g.viewCount + :cached
                where g.id = :gppId and g.deletedAt is null
    """)
    void increaseViewCountBatch(@Param("gppId") Long gppId, @Param("cached") Long cached);

    /**
     * 모집상태 변경 대상의 상태를 즉시 변경 - [삭제처리 제외]
     * - 엔티티 생성/로딩 없이 DB에서 직접 한 번에 처리
     * - 영속성 컨테이너의 관리를 받지않고 즉시 조작하므로, 스케줄러 외의 곳에서는 사용 X
     * - 영속성 컨텍스트의 엔티티에서 접근하여 조회한 상태와 불일치 가능
     * (엔티티를 관리하는 영속성 컨텍스트[1차 캐시] vs 아래의 벌크 업데이트 쿼리메서드는 즉시 DB조작)
     * - 한 트랜잭션 안에서 영속상태의 엔티티를 로드->접근하는 코드 사이에 아래의 벌크업데이트가 공존할 경우, 데이터 불일치
     * - 벌크 업데이트 쿼리의 반환값은 조작 수(영향받은 row count), 따라서 데이터 타입은 int
     */
    // READY -> DOING
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update GroupPurchasePost g
                set g.gppProgressStatus = 'DOING'
                where g.deletedAt is null
                  and g.gppProgressStatus = 'READY'
                  and g.startDate <= :now
    """)
    int updateReadyToDoing(@Param("now") LocalDateTime now);

    // DOING -> END
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                update GroupPurchasePost g
                set g.gppProgressStatus = 'END'
                where g.deletedAt is null
                  and g.gppProgressStatus = 'DOING'
                  and g.endDate <= :now
    """)
    int updateDoingToEnd(@Param("now") LocalDateTime now);

    // id에 대응되는 GPP 리스트 조회 - [삭제처리 제외]
    List<GroupPurchasePost> findAllByIdInAndDeletedAtIsNull(List<Long> ids);

    // ViewCount 랭킹 조회
    List<GroupPurchasePost> findTop10ByDeletedAtIsNullOrderByViewCountDesc();

    // 엘라스틱서치 전체 역 벌크인덱싱용
    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<GroupPurchasePost> findAll(Pageable pageable);

    // 엘라스틱서치 역 벌크인덱싱 업데이트용
    @EntityGraph(attributePaths = {"user"})
    Page<GroupPurchasePost> findAllByUpdatedAtAfter(LocalDateTime modifiedAfter, PageRequest of);
}