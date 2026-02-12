package com.zero9platform.domain.admin.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.admin.model.response.order.OrderPaymentDetailResponse;
import com.zero9platform.domain.admin.model.response.user.UserDetailResponse;
import com.zero9platform.domain.order.repository.OrderRepository;
import com.zero9platform.domain.user.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerDetailResponse;
import com.zero9platform.domain.user.repository.UserRepository;
import com.zero9platform.domain.user.entity.Influencer;
import com.zero9platform.domain.admin.model.request.influencer.InfluencerApproveRequest;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final InfluencerRepository influencerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * 인플루언서 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<InfluencerDetailResponse> influencerList(Boolean status, String nickname, Pageable pageable) {

        return influencerRepository.findByApprovalStatusAndUser(status, nickname, pageable)
                .map(InfluencerDetailResponse::from);
    }

    /**
     * 인플루언서 가입 승인
     */
    @Transactional
    public InfluencerApproveResponse influencerApprove(Long userId, InfluencerApproveRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Influencer influencer = influencerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_INFLUENCER));

        // request 에서 true로 입력 하고 이미 상태값이 true이면 예외처리
        if (request.getApprove() == true && influencer.isApproved()) {
            throw new CustomException(ExceptionCode.USER_INFLUENCER_ALREADY_APPROVED);
        }

        // 인플루언서 승인
        influencer.influencerApprove(request.getApprove());

        return InfluencerApproveResponse.from(influencer);
    }

    /**
     * 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> userList(String nickname, Pageable pageable) {

        return userRepository.findAllUser(nickname, pageable)
                .map(UserDetailResponse::from);
    }

    /**
     * 관리자 결제 내역 조회
     */
    @Transactional(readOnly = true)
    public List<OrderPaymentDetailResponse> orderPaymentList(String status) {

        return orderRepository.findPaidOrderPaymentList(status);
    }
}