package com.zero9platform.domain.clickLog;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.clickLog.model.ClickLogProductPostDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommonResponse<ClickLogProductPostDetailResponse>> clickLogProductPostDetailHandler(@PathVariable Long productPostId) {

        ClickLogProductPostDetailResponse response = clickLogService.productPostDetail(productPostId, null);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 상세 조회 성공", response));
    }
}