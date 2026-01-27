package com.zero9platform.domain.clickLog.response;

import com.zero9platform.domain.clickLog.entity.ClickLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {
}
