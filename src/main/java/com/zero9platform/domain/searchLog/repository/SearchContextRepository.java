package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.searchLog.entity.SearchContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchContextRepository extends JpaRepository<SearchContext, Long> {

    @Query("""
            SELECT sc
            FROM SearchContext sc
            WHERE sc.userId = :userId
              AND sc.productPostId = :productPostId
              AND sc.keyword IS NOT NULL
            ORDER BY sc.createdAt DESC
            """)
    List<SearchContext> findLatestContext(@Param("userId") Long userId,
                                          @Param("productPostId") Long productPostId,
                                          Pageable pageable);
}
