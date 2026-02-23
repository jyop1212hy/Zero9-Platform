package com.zero9platform.domain.comment.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.FeedType;
import com.zero9platform.common.enums.PostType;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.activity_feed.event.FeedCreateEvent;
import com.zero9platform.domain.comment.entity.Comment;
import com.zero9platform.domain.comment.model.request.CommentCreateRequest;
import com.zero9platform.domain.comment.model.request.CommentUpdateRequest;
import com.zero9platform.domain.comment.model.response.CommentCreateResponse;
import com.zero9platform.domain.comment.model.response.CommentGetListResponse;
import com.zero9platform.domain.comment.repository.CommentRepository;
import com.zero9platform.domain.post.entity.Post;
import com.zero9platform.domain.post.repository.PostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 공지 / 문의  답변(댓글) 작성
     */
    @Transactional
    public CommentCreateResponse commentCreate(Long userId, Long postId, CommentCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        // 정책 검증 실행
        validateCommentPolicy(post, user);

        Comment saved = commentRepository.save(new Comment(post, user, request.getContent()));

        // 문의사항(INQUIRY)에 답변이 달린 경우에만 답변완료 피드 발행
        if (PostType.INQUIRY.name().equals(post.getType())) {
            eventPublisher.publishEvent(new FeedCreateEvent(
                    FeedType.INQUIRY_ANSWER,
                    post.getId(),
                    post.getTitle(),
                    post.getUser().getId() // 알림 받을 질문자 ID
            ));
        }
        return CommentCreateResponse.from(saved);
    }

    /**
     * 공지 / 문의  답변(댓글)  전체목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<CommentGetListResponse> commentGetPage(Long userId, Long postId, Pageable pageable) {

        // 비밀글 여부 확인
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        // 권한 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 비밀글이 아니거나 관리자거나 작성자 본인이거나
        boolean hasAccess = !post.isSecret() ||
                UserRole.ADMIN.name().equals(user.getRole()) ||
                post.getUser().getId().equals(userId);

        Page<CommentGetListResponse> page = commentRepository.findAllByPostId(postId, pageable)
                .map(comment -> hasAccess
                        ? CommentGetListResponse.from(comment)
                        : CommentGetListResponse.maskContent(comment));

        return PageResponse.from(page);
    }

    /**
     * 공지 / 문의  답변(댓글) 수정
     */
    @Transactional
    public void commentUpdate(Long userId, Long postId, Long commentId, CommentUpdateRequest request) {

        postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_COMMENT));

        validateCommentBelongsToPost(comment, postId);

        validOwner(comment, userId);

        comment.update(request.getContent());
    }

    /**
     * 공지 / 문의  답변(댓글) 삭제
     */
    @Transactional
    public void commentDelete(Long userId, Long postId, Long commentId) {

        postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_COMMENT));

        validateCommentBelongsToPost(comment, postId);

        validOwner(comment, userId);

        commentRepository.delete(comment);
    }

    /**
     * 댓글 작성자 본인 여부 검증
     */
    private void validOwner(Comment comment, Long userId) {

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }
    }

    /**
     *  게시물에 속한 댓글인지 검증
     */
    private void validateCommentBelongsToPost(Comment comment, Long postId) {

        if (!comment.getPost().getId().equals(postId)) {
            throw new CustomException(ExceptionCode.NOT_FOUND_COMMENT);
        }
    }

    private void validateCommentPolicy(Post post, User user) {
        String type = post.getType();
        boolean isAdmin = UserRole.ADMIN.name().equals(user.getRole());

        // 1. 공지사항(NOTICE): 댓글 불가
        if (PostType.NOTICE.name().equals(type)) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        // 2. 문의사항(INQUIRY): 관리자만 1개 작성 가능
        if (PostType.INQUIRY.name().equals(type)) {
            if (!isAdmin) throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
            if (commentRepository.countByPostId(post.getId()) >= 1) {
                throw new CustomException(ExceptionCode.COMMENT_ALREADY_EXISTS);
            }
        }
    }
}