package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.searchLog.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
}
