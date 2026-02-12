package com.zero9platform.domain.gpp_comment.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.gpp_comment.model.request.GppCommentCreateRequest;
import com.zero9platform.domain.gpp_comment.model.request.GppCommentGetListRequest;
import com.zero9platform.domain.gpp_comment.model.request.GppCommentUpdateRequest;
import com.zero9platform.domain.gpp_comment.model.response.GppCommentCreateResponse;
import com.zero9platform.domain.gpp_comment.model.response.GppCommentGetListResponse;
import com.zero9platform.domain.gpp_comment.service.GppCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/gpp-comments")
public class GppCommentController {

    private final GppCommentService gppCommentService;

    /**
     * 공동구매 게시물 댓글 작성
     */
    @PostMapping
    public ResponseEntity<CommonResponse<GppCommentCreateResponse>> gppCommentCreateHandler(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody GppCommentCreateRequest request) {

        GppCommentCreateResponse response = gppCommentService.gppCommentCreate(authUser.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("공동구매 게시물 댓글 생성 성공", response));
    }

    /**
     * 공동구매 게시물 댓글 전체목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<GppCommentGetListResponse>>> gppCommentGetPageHandler(@RequestBody GppCommentGetListRequest request, Pageable pageable) {

        PageResponse<GppCommentGetListResponse> response = PageResponse.from(gppCommentService.gppCommentGetPage(request, pageable));

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 댓글 조회 성공", response));
    }

    /**
     * 공동구매 게시물 댓글 수정
     */
    @PutMapping("/{gppCommentId}")
    public ResponseEntity<CommonResponse<Void>> gppCommentUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long gppCommentId, @Valid @RequestBody GppCommentUpdateRequest request) {

        gppCommentService.gppCommentUpdate(authUser.getId(), gppCommentId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 댓글 수정 성공", null));
    }

    /**
     * 공동구매 게시물 댓글 삭제
     */
    @DeleteMapping("/{gppCommentId}")
    public ResponseEntity<CommonResponse<Void>> gppCommentDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long gppCommentId) {

        gppCommentService.gppCommentDelete(authUser.getId(), gppCommentId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 댓글 삭제 성공", null));
    }
}