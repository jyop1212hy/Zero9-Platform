package com.zero9platform.domain.comment.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
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

    /**
     * 일반 게시물 댓글 작성
     */
    @Transactional
    public CommentCreateResponse commentCreate(Long userId, Long postId, CommentCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Comment saved = commentRepository.save(new Comment(post, user, request.getContent()));

        return CommentCreateResponse.from(saved);
    }

    /**
     * 일반 게시물 댓글 전체목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<CommentGetListResponse> commentGetPage(Long postId, Pageable pageable) {

        postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Page<CommentGetListResponse> page = commentRepository.findAllByPostId(postId, pageable)
                .map(CommentGetListResponse::from);

        return PageResponse.from(page);
    }

    /**
     * 일반 게시물 댓글 수정
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
     * 일반 게시물 댓글 삭제
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
}