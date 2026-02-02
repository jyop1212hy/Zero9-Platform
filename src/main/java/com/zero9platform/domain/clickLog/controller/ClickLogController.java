package com.zero9platform.domain.clickLog.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.clickLog.model.response.ClickLogProductPostDetailResponse;
import com.zero9platform.domain.clickLog.service.ClickLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/click-log")
public class ClickLogController {

    private final ClickLogService clickLogService;

    /**
     * 상품 상세 조회
     * - 검색 유입일 경우 keyword 함께 전달
     */
    @GetMapping("/product-posts/{productPostId}")
    public ResponseEntity<CommonResponse<ClickLogProductPostDetailResponse>> clickLogProductPostDetailHandler(@PathVariable Long productPostId,
                                                                                                              @AuthenticationPrincipal AuthUser authUser) {

        // 실제 상품 상세 데이터 조회 (예시 서비스 호출)
        ClickLogProductPostDetailResponse response = clickLogService.productDetailClick(productPostId, authUser);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 상세 조회 성공", response));
    }

}