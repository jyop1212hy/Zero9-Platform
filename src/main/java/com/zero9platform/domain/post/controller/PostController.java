package com.zero9platform.domain.post.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.post.model.response.PostGetListResponse;
import com.zero9platform.domain.post.service.PostService;
import com.zero9platform.domain.post.model.request.PostCreateRequest;
import com.zero9platform.domain.post.model.request.PostUpdateRequest;
import com.zero9platform.domain.post.model.response.PostCreateResponse;
import com.zero9platform.domain.post.model.response.PostGetDetailResponse;
import com.zero9platform.domain.post.model.response.PostUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/posts")
public class PostController {

    private final PostService postService;

    /**
     *  일반 게시물 생성
     */
    @PostMapping
    public ResponseEntity<CommonResponse<PostCreateResponse>> postCreateHandler(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody PostCreateRequest request) {

        PostCreateResponse response = postService.postCreate(authUser.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("일반 게시물 생성 성공", response));
    }

    /**
     *  일반 게시물 상세조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<CommonResponse<PostGetDetailResponse>> postGetDetailHandler(@PathVariable Long postId) {

        PostGetDetailResponse response = postService.postGetDetail(postId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 상세조회 성공", response));
    }

    /**
     *  일반 게시물 전체목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<PostGetListResponse>>> postGetListHandler(Pageable pageable){

        PageResponse<PostGetListResponse> response = PageResponse.from(postService.postGetPage(pageable));

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 목록조회 성공", response));
    }

    /**
     *  일반 게시물 수정
     */
    @PutMapping("/{postId}")
    public ResponseEntity<CommonResponse<PostUpdateResponse>> postUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId, @Valid @RequestBody PostUpdateRequest request) {

        PostUpdateResponse response = postService.postUpdate(authUser.getId(), postId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 수정 성공", response));
    }

    /**
     *  일반 게시물 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<CommonResponse<Void>> postDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long postId) {

        postService.postDelete(authUser, postId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("일반 게시물 삭제 성공", null));
    }
}