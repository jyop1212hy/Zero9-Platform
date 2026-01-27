package com.zero9platform.domain.activity_feed.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.activity_feed.model.response.ActivityFeedResponse;
import com.zero9platform.domain.activity_feed.service.ActivityFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zero9/feeds")
@RequiredArgsConstructor
public class ActivityFeedController {

    private final ActivityFeedService feedService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<ActivityFeedResponse>>> feedsGetListHandler() {

        List<ActivityFeedResponse> feeds = feedService.feedsGetList();

        return ResponseEntity.ok(CommonResponse.success("액티비티 피드 조회 성공", feeds));
    }
}

