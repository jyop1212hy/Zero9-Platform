package com.zero9platform.domain.user.Service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.GppApprovalStatus;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.admin.entity.Influencer;
import com.zero9platform.domain.comment.repository.CommentRepository;
import com.zero9platform.domain.gpp_comment.repository.GppCommentRepository;
import com.zero9platform.domain.gpp_follow.model.response.InfluencerGppGetListResponse;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GppRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.model.influencer.InfluencerDetailResponse;
import com.zero9platform.domain.admin.repository.InfluencerRepository;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluencerService {

    private final InfluencerRepository influencerRepository;
    private final UserRepository userRepository;
    private final GppRepository gppRepository;
    private final GppCommentRepository gppCommentRepository;
    private final CommentRepository commentRepository;

    /**
     * 인플루언서 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<InfluencerDetailResponse> influencerList(Boolean status, Pageable pageable) {

        return influencerRepository.findByApprovalStatusAndUser(status, pageable)
                .map(InfluencerDetailResponse::from);

    }

    /**
     * 인플루언서별 공동구매 게시물 목록 조회
     */
//    @Transactional
//    public PageResponse<InfluencerGppGetListResponse> influencerGppFollowGetList(Long influencerId, Pageable pageable) {
//
//        // 인플루언서인지 조회
//        User user = userRepository.findById(influencerId)
//                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
//
//        if (UserRole.valueOf(user.getRole()) != UserRole.INFLUENCER) {
//            throw new CustomException(ExceptionCode.USER_IS_NOT_INFLUENCER);
//        }
//
//        // 인플루언서 (해당하는 userId) 의 공동구매 게시물 조회 (삭제되지 않고, 승인된 공동구매 게시물)
//        Page<GroupPurchasePost> gppPage = gppRepository.findAllByDeletedAtIsNullAndGppApprovalStatusAndUser_Id(GppApprovalStatus.APPROVED, user.getId(), pageable);
//
//        for (GroupPurchasePost gpp :gppPage) {
//            Long gppId = gpp.getId();
//            Long commentCount = commentRepository.countByGppTotalComments(gppId);
//            InfluencerGppGetListResponse response = InfluencerGppGetListResponse.from(gpp, commentCount);
//
//        }
//
////        Page<InfluencerGppGetListResponse> pageMap = PageImpl<>(InfluencerGppGetListResponse.from(gpp, commentCount));
//
//        return PageResponse.from(response);
//    }
}
