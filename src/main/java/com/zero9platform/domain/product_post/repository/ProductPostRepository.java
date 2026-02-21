package com.zero9platform.domain.product_post.repository;

import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.model.response.ProductPostGetMyListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductPostRepository extends JpaRepository<ProductPost, Long> {

    Page<ProductPost> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    @Query("""
        SELECT pp
        FROM ProductPost pp
        JOIN pp.user u
        WHERE ((:condition = 'product_title' AND pp.title LIKE CONCAT('%', :keyword, '%'))
        OR(:condition = 'product_name' AND pp.name LIKE CONCAT('%', :keyword, '%'))
        OR(:condition = 'influencer' AND u.nickname LIKE CONCAT('%', :keyword, '%'))
        OR((:condition IS NULL OR :condition = '') AND (pp.title LIKE CONCAT('%', :keyword, '%') OR pp.name LIKE CONCAT('%', :keyword, '%') OR u.nickname LIKE CONCAT('%', :keyword, '%'))))
    """)
    Page<ProductPost> searchByKeyword(@Param("keyword") String keyword, @Param("condition") String searchCondition, Pageable pageable);

    // 엘라스틱서치 전체 역 벌크인덱싱용
    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<ProductPost> findAll(Pageable pageable);

    // 엘라스틱서치 역 벌크인덱싱 업데이트용
    @EntityGraph(attributePaths = {"user"})
    Page<ProductPost> findAllByUpdatedAtAfter(LocalDateTime modifiedAfter, PageRequest of);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update ProductPost p
    set p.progressStatus = 'DOING'
    where p.progressStatus = 'READY'
    and p.startDate <= :now
    and p.endDate > :now
    """)
    int updateToDoing(@Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update ProductPost p
    set p.progressStatus = 'END'
    where p.progressStatus != 'END'
    and p.endDate <= :now
    """)
    int updateToEnd(@Param("now") LocalDateTime now);

    // 상태별로 가장 최근 업데이트된 상품 하나만 가져오는 쿼리 메서드
    Optional<ProductPost> findFirstByProgressStatusOrderByUpdatedAtDesc(String progressStatus);

    // 내일 마감예정
    @Query("""
        SELECT p FROM ProductPost p
        WHERE p.progressStatus = 'DOING'
        AND p.endDate > :now
        AND p.endDate <= :tomorrow
    """)
    List<ProductPost> findDeadlinePosts(@Param("now") LocalDateTime now, @Param("tomorrow") LocalDateTime tomorrow);

    // 내일 오픈예정
    @Query("""
        SELECT p FROM ProductPost p
        WHERE p.progressStatus = 'READY'
        AND p.startDate > :now
        AND p.startDate <= :tomorrow
    """)
    List<ProductPost> findUpcomingPosts(@Param("now") LocalDateTime now, @Param("tomorrow") LocalDateTime tomorrow);

    // 내가 작성한 판매 게시물 리미트 버전
    @Query("""
        SELECT new com.zero9platform.domain.product_post.model.response.ProductPostGetMyListResponse(
            p.id,
            p.title,
            p.originalPrice,
            p.startDate,
            p.endDate,
            count(f)
        )
        FROM ProductPost p
        LEFT JOIN ProductPostFavorite f ON f.productPost.id = p.id
        WHERE p.user.id = :userId
        GROUP BY p
        ORDER BY p.createdAt DESC
    """)
    List<ProductPostGetMyListResponse> findMyPostsWithFavoriteCount(@Param("userId") Long userId, Pageable pageable);
}