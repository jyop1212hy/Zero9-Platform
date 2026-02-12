package com.zero9platform.domain.gpp_follow;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.gpp_follow.entity.GppFollow;
import com.zero9platform.domain.gpp_follow.model.response.GppFollowGetDetailResponse;
import com.zero9platform.domain.gpp_follow.repository.FollowRepository;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GppRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final GppRepository gppRepository;
    private final FollowRepository followRepository;

    /**
     * 공동구매 게시물 일정 팔로우
     */
    @Transactional
    public void gppFollowCreate(Long userId, Long gppId) {

        // 유저 조회 - 토큰 추가 후 롤 권한 확인 필요 체크
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 공동구매 게시물 조회
        GroupPurchasePost gpp = gppRepository.findById(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        // 이미 팔로우 했으면 예외 처리
        boolean existence = followRepository.existsByUserIdAndGroupPurchasePostId(user.getId(), gpp.getId());

        if (existence) {
            throw new CustomException(ExceptionCode.GROUP_PURCHASE_POST_ALREADY_SUBSCRIBED);
        }

        GppFollow gppFollow = new GppFollow(user, gpp);

        followRepository.save(gppFollow);
    }

    /**
     * 공동구매 게시물 일정 팔로우 취소
     */
    @Transactional
    public void gppFollowDelete(Long userId, Long gppId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        GroupPurchasePost gpp = gppRepository.findById(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        Optional<GppFollow> gppFollow = followRepository.findByUserIdAndGroupPurchasePostId(user.getId(), gpp.getId());

        if (gppFollow.isEmpty()) {
            throw new CustomException(ExceptionCode.GROUP_PURCHASE_POST_ALREADY_SUBSCRIBED);
        }

        followRepository.deleteById(gppFollow.get().getId());
    }

    /**
     * 공동구매 게시물 일정 팔로우 목록 조회 - 추후 Page 및 stream 변환 작업
     */
    @Transactional(readOnly = true)
    public PageResponse<GppFollowGetDetailResponse> gppFollowGetPage(Long userId, Pageable pageable) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Page<GroupPurchasePost> gpp = gppRepository.findByUserIdAndFollowGpp(userId, pageable);

        Page<GppFollowGetDetailResponse> pageMap = gpp.map(GppFollowGetDetailResponse::from);

        return PageResponse.from(pageMap);
    }
}