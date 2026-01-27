package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.searchLog.SearchContext;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SearchContextRepository extends JpaRepository<SearchContext, Long> {

    Optional<SearchContext> findTopByProductPostIdOrderByCreatedAtDesc(Long productPostId);
}