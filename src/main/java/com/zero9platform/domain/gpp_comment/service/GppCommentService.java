package com.zero9platform.domain.gpp_comment.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.gpp_comment.entity.GppComment;
import com.zero9platform.domain.gpp_comment.model.request.GppCommentCreateRequest;
import com.zero9platform.domain.gpp_comment.model.request.GppCommentGetListRequest;
import com.zero9platform.domain.gpp_comment.model.request.GppCommentUpdateRequest;
import com.zero9platform.domain.gpp_comment.model.response.GppCommentCreateResponse;
import com.zero9platform.domain.gpp_comment.model.response.GppCommentGetListResponse;
import com.zero9platform.domain.gpp_comment.repository.GppCommentRepository;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GppCommentService {

    private final GppCommentRepository gppCommentRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final UserRepository userRepository;

    /**
     * 공동구매 게시물 댓글 작성
     */
    @Transactional
    public GppCommentCreateResponse gppCommentCreate(Long userId, GppCommentCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        GroupPurchasePost gpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(request.getGppId())
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        GppComment saved = gppCommentRepository.save(new GppComment(gpp, user, request.getContent()));

        return GppCommentCreateResponse.from(saved);
    }

    /**
     * 공동구매 게시물 댓글 전체목록 조회
     */
    @Transactional(readOnly = true)
    public Page<GppCommentGetListResponse> gppCommentGetPage(GppCommentGetListRequest request, Pageable pageable) {

        GroupPurchasePost existingGpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(request.getGppId())
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        Page<GppComment> page = gppCommentRepository.findAllByGroupPurchasePost_Id(existingGpp.getId(), pageable);

        return page.map(GppCommentGetListResponse::from);
    }

    /**
     * 공동구매 게시물 댓글 수정
     */
    @Transactional
    public void gppCommentUpdate(Long userId, Long gppCommentId, GppCommentUpdateRequest request) {

        GppComment gppComment = gppCommentRepository.findById(gppCommentId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_COMMENT));

        validOwner(gppComment, userId);

        gppComment.update(request.getContent());
    }

    /**
     * 공동구매 게시물 댓글 삭제
     */
    @Transactional
    public void gppCommentDelete(Long userId, Long gppCommentId) {

        GppComment gppComment = gppCommentRepository.findById(gppCommentId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_COMMENT));

        validOwner(gppComment, userId);

        gppCommentRepository.delete(gppComment);
    }

    /**
     * 댓글 작성자 본인 여부 검증
     */
    private void validOwner(GppComment gppComment, Long userId) {

        if (!gppComment.getUser().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }
}
