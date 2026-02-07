package com.zero9platform.domain.grouppurchase_post.controller;

import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.grouppurchase_post.model.request.GroupPurchasePostCreateRequest;
import com.zero9platform.domain.grouppurchase_post.model.request.GroupPurchasePostUpdateRequest;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostDetailResponse;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostListResponse;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostReadResponse;
import com.zero9platform.domain.grouppurchase_post.service.GroupPurchasePostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.zero9platform.domain.auth.model.AuthUser;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class GroupPurchasePostController {

    private final GroupPurchasePostService gppService;

    /**
     * 공동구매 게시물 작성
     */
    @PostMapping("/gp-posts")
    public ResponseEntity<CommonResponse<GroupPurchasePostDetailResponse>> GPPCreateHandler(@RequestPart("gppCreateRequest") @Valid GroupPurchasePostCreateRequest request, @AuthenticationPrincipal AuthUser authUser, @RequestPart(value = "contentImage", required = false) MultipartFile file) {

        if (authUser.getUserRole() != UserRole.INFLUENCER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonResponse.fail("공동구매 게시물 작성은 인플루언서만 가능"));
        }

        GroupPurchasePostDetailResponse response = gppService.gpPostCreate(request, authUser.getId(), file);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 작성 성공", response));
    }

    /**
     * 공동구매 게시물 전체 조회
     */
    @GetMapping("/gp-posts")
    public ResponseEntity<CommonResponse<PageResponse<GroupPurchasePostListResponse>>> GPPReadListHandler(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        PageResponse<GroupPurchasePostListResponse> response = PageResponse.from(gppService.gpPostReadAll(pageable));

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 목록 조회 성공", response));
    }

    /**
     * 공동구매 게시물 상세 조회
     */
    @GetMapping("/gp-posts/{gppId}")
    public ResponseEntity<CommonResponse<GroupPurchasePostReadResponse>> GPPReadDetailHandler(@PathVariable Long gppId) {

        GroupPurchasePostReadResponse response = gppService.gpPostReadDetail(gppId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 상세 조회 성공", response));
    }

    /**
     * 공동구매 게시물 수정
     */
    @PutMapping("/gp-posts/{gppId}")
    public ResponseEntity<CommonResponse<GroupPurchasePostDetailResponse>> GPPUpdateHandler(@PathVariable Long gppId, @RequestPart("gppUpdateRequest") @Valid GroupPurchasePostUpdateRequest request, @AuthenticationPrincipal AuthUser authUser, @RequestPart(value = "contentImage", required = false) MultipartFile file) {

        if (authUser.getUserRole() != UserRole.INFLUENCER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonResponse.fail("공동구매 게시물 수정은 인플루언서, 본인만 가능"));
        }

        GroupPurchasePostDetailResponse response = gppService.gpPostUpdate(gppId, request, authUser.getId(), file);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 수정 성공", response));
    }

    /**
     * 공동구매 게시물 삭제
     */
    @DeleteMapping("/gp-posts/{gppId}")
    public ResponseEntity<CommonResponse<Void>> GPPDeleteHandler(@PathVariable Long gppId, @AuthenticationPrincipal AuthUser authUser) {

        boolean isAdmin = false;
        if (authUser.getUserRole() == UserRole.ADMIN) {
            isAdmin = true;
        }

        gppService.gpPostDelete(gppId, authUser.getId(), isAdmin);

        return ResponseEntity.ok(CommonResponse.success("공동구매 게시물 삭제 성공", null));
    }
}
