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
