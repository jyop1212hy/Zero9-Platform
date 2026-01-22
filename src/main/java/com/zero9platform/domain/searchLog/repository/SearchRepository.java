package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.searchLog.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchRepository extends JpaRepository<SearchLog, Long> {

    // 키워드 저장
    Optional<SearchLog> findByKeyword(String keyword);
}
