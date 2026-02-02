package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.ranking.model.response.SearchLogRankingAggregateResponse;
import com.zero9platform.domain.searchLog.entity.SearchLog;
import com.zero9platform.domain.searchLog.model.SearchLogListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    // 키워드 저장
    Optional<SearchLog> findByKeyword(String keyword);

    // 인기검색어 차트(상품명)
    @Query("""
                SELECT new com.zero9platform.domain.searchLog.model.SearchLogListResponse(
                    ROW_NUMBER() OVER (ORDER BY s.count DESC) AS rank,
                    s.keyword,
                    s.count
                )
                FROM SearchLog s
                ORDER BY s.count DESC
            """)
    List<SearchLogRankingAggregateResponse> findTopKeywords(Pageable pageable);

}
