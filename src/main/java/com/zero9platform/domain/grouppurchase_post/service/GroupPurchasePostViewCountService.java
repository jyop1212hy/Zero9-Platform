package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/**
 * 조회수 즉시 증가
 * - (동시성/정합성 문제는 없음)

 * - 인기 게시물에 트래픽이 몰릴 경우, 하나의 row를 대상으로 Update를 한줄씩 처리하는 병목이 발생
 *  * - (일괄처리 X, 트래픽 대응 안됨)

 * - (Redis를 사용할 수 없는 환경일 경우 사용 가능)
 */
public class GroupPurchasePostViewCountService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;

    // propagation = Propagation.REQUIRES_NEW : 기존 트랜잭션이 있더라도 무조건 새 트랜잭션을 시작
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void increaseViewCount(Long gppId) {

        int updated = groupPurchasePostRepository.increaseViewCount(gppId);

        if (updated == 0) {
            throw new CustomException(ExceptionCode.GPP_NOT_FOUND);
        }
    }
}