package com.zero9platform.domain.admin;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.admin.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import com.zero9platform.domain.admin.entity.Influencer;
import com.zero9platform.domain.admin.model.request.influencer.InfluencerApproveRequest;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerApproveResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final InfluencerRepository influencerRepository;
    private final UserRepository userRepository;

    /**
     * 인플루언서 가입 승인
     */
    @Transactional
    public InfluencerApproveResponse influencerApprove(Long userId, InfluencerApproveRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Influencer influencer = influencerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_IS_NOT_INFLUENCER));

        // request 에서 true로 입력 하고 이미 상태값이 true이면 예외처리
        if (request.getApprove() == true && influencer.isApproved()) {
            throw new CustomException(ExceptionCode.INFLUENCER_ALREADY_APPROVED);
        }

        // 인플루언서 승인
        influencer.influencerApprove(request.getApprove());

        return InfluencerApproveResponse.from(influencer);
    }
}
