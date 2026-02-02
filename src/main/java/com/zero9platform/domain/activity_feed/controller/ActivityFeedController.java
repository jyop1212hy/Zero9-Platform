package com.zero9platform.domain.activity_feed.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.service.ActivityFeedService;
import com.zero9platform.domain.auth.model.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/zero9/feeds")
@RequiredArgsConstructor
public class ActivityFeedController {

    private final ActivityFeedService feedService;

    /**
     * 전체 피드 조회
     */
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<PageResponse<ActivityFeedResponse>>> feedsGetListHandler(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<ActivityFeedResponse> response = PageResponse.from(feedService.feedsGetList(pageable));

        return ResponseEntity.ok(CommonResponse.success("전체 피드 목록 조회 성공", response));
    }

    /**
     * 로그인한 유저의 본인 피드 확인
     */
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<PageResponse<ActivityFeedResponse>>> feedsGetMyListHandler(@AuthenticationPrincipal AuthUser authUser, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponse<ActivityFeedResponse> response = PageResponse.from(feedService.feedsGetMyList(authUser.getId(), pageable));

        return ResponseEntity.ok(CommonResponse.success("내 피드 목록 조회 성공", response));
    }
}

