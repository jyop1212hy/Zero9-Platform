package com.zero9platform.domain.product_post.repository;

import com.zero9platform.domain.product_post.entity.ProductPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
}
