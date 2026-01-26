package com.zero9platform.domain.product_post.repository;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.product_post.entity.ProductPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductPostRepository extends JpaRepository<ProductPost, Long> {

    Optional<ProductPost> findByIdAndDeletedAtIsNull(Long productpostId);

    Page<ProductPost> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("""
                SELECT pp
                FROM ProductPost pp
                JOIN FETCH pp.user u
                WHERE pp.deletedAt IS NULL
                  AND (
                    (:searchCondition = 'product_title' AND pp.title LIKE CONCAT('%', :keyword, '%'))
                    OR
                    (:searchCondition = 'product_name' AND pp.product.name LIKE CONCAT('%', :keyword, '%'))
                    OR
                    (:searchCondition = 'influencer' AND u.nickname LIKE CONCAT('%', :keyword, '%'))
                    OR
                    ((:searchCondition IS NULL OR :searchCondition = '') AND (pp.title LIKE CONCAT('%', :keyword, '%') OR pp.product.name LIKE CONCAT('%', :keyword, '%') OR u.nickname LIKE CONCAT('%', :keyword, '%')))
                  )
            """)
    Page<ProductPost> searchByKeyword(@Param("keyword") String keyword, @Param("searchCondition") String searchCondition, Pageable pageable);
//    Page<ProductPost> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("select distinct pp from ProductPost pp " +
            "join pp.productPostOptionList o " +
            "where pp.deletedAt is null " +
            "and o.optionStatus = 'ACTIVE'")
    Page<ProductPost> findAllVisible(Pageable pageable);
}
