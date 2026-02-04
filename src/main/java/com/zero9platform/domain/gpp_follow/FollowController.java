package com.zero9platform.domain.gpp_follow;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.gpp_follow.model.response.GppFollowGetDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * 공동구매 게시물 일정 팔로우
     */
    @PostMapping("/{gppId}/follows")
    public ResponseEntity<CommonResponse<Void>> gppFollowCreateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long gppId) {

        Long userId = authUser.getId();

        followService.gppFollowCreate(userId, gppId);   // 사용자가 공동구매 게시물을 구독

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 일정 팔로우 성공", null));
    }

    /**
     * 공동구매 게시물 일정 팔로우 취소
     */
    @DeleteMapping("/{gppId}/follows")
    public ResponseEntity<CommonResponse<Void>> gppFollowDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long gppId) {

        Long userId = authUser.getId();

        followService.gppFollowDelete(userId, gppId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 일정 팔로우 취소 성공", null));
    }

    /**
     * 공동구매 게시물 일정 팔로우 목록 조회
     */
    @GetMapping("/follows")
    public ResponseEntity<CommonResponse<PageResponse<GppFollowGetDetailResponse>>> gppFollowGetListHandler(@AuthenticationPrincipal AuthUser authUser, Pageable pageable) {

        Long userId = authUser.getId();

        PageResponse<GppFollowGetDetailResponse> gppList = followService.gppFollowGetPage(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("공동구매 게시물 일정 팔로우 목록 조회 성공", gppList));
    }
}
