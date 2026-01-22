package com.zero9platform.domain.comment.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.comment.model.request.CommentUpdateRequest;
import com.zero9platform.domain.comment.service.CommentService;
import com.zero9platform.domain.comment.model.request.CommentCreateRequest;
import com.zero9platform.domain.comment.model.response.CommentCreateResponse;
import com.zero9platform.domain.comment.model.response.CommentGetListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 일반 게시물 댓글 작성
     */
    @PostMapping
    public ResponseEntity<CommonResponse<CommentCreateResponse>> commentCreateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId, @Valid @RequestBody CommentCreateRequest request) {


        CommentCreateResponse response = commentService.commentCreate(authUser.getId(), postId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("댓글 생성 성공", response));
    }

    /**
     * 일반 게시물 댓글 전체목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<CommentGetListResponse>>> commentGetPageHandler(@PathVariable Long postId, Pageable pageable) {

        PageResponse<CommentGetListResponse> response = commentService.commentGetPage(postId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("댓글 조회 성공", response));
    }

    /**
     * 일반 게시물 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResponse<Void>> commentUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId, @PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequest request) {

        commentService.commentUpdate(authUser.getId(), postId, commentId, request);

        return ResponseEntity.ok(CommonResponse.success("댓글 수정 성공", null));
    }

    /**
     * 일반 게시물 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResponse<Void>> commentDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId, @PathVariable Long commentId) {

        commentService.commentDelete(authUser.getId(), postId, commentId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("댓글 삭제 성공", null));
    }
}