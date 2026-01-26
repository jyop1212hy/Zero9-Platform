package com.zero9platform.domain.user.Service;

import com.zero9platform.common.enums.ExceptionCode;
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

}
