package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupPurchasePostScheduler {

    private final GroupPurchasePostRepository groupPurchasePostRepository;

    /**
     * 매일 00시에 모집 상태 자동 변경
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(cron = "0 */10 * * * *") // 10분마다
    public void updateGppProgressStatus() {

        LocalDateTime now = LocalDateTime.now();

        // 오늘을 기준으로 모집상태 변경이 필요한 대상 타겟 조회
        List<GroupPurchasePost> targets = groupPurchasePostRepository.findProgressStatusChangeTargets(now);

        // 대상 모집상태 업데이트
        for (GroupPurchasePost gpp : targets) {
            gpp.updateProgressStatus(now);
        }
        // Dirty Checking + 자동 flush

        log.info("GPP 상태 변경 대상 수: {}", targets.size());
    }

//    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
//    public void updateGppProgressStatus() {
//
//        LocalDateTime now = LocalDateTime.now();
//
//        int readyToDoing = groupPurchasePostRepository.updateReadyToDoing(now);
//        int doingToEnd = groupPurchasePostRepository.updateDoingToEnd(now);
//
//        log.info("GPP 상태 변경 - READY->DOING: {}, DOING->END: {}", readyToDoing, doingToEnd);
//    }
}
