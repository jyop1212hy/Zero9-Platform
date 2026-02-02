package com.zero9platform.domain.product_post_option.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.model.response.*;
import com.zero9platform.domain.product_post_option.service.ProductPostOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class ProductPostOptionController {

    private final ProductPostOptionService postOptionService;

    /**
     * 옵션 추가 생성
     */
    @PostMapping("/product-posts/{productPostId}/options")
    public ResponseEntity<CommonResponse<ProductPostOptionCreateResponse>> optionCreateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productPostId, @RequestBody @Valid ProductPostOptionCreateRequest request) {

        ProductPostOptionCreateResponse response = postOptionService.optionCreate(authUser.getId(), authUser.getUserRole(), productPostId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("옵션 추가 생성 성공", response));
    }

    /**
     * 옵션 상세 조회
     */
    @GetMapping("/options/{optionId}")
    public ResponseEntity<CommonResponse<ProductPostOptionGetDetailResponse>> optionCreateHandler(@PathVariable Long optionId) {

        ProductPostOptionGetDetailResponse response = postOptionService.optionGetDetail(optionId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("옵션 상세 조회 성공", response));
    }

    /**
     * 옵션 삭제
     * */
    @DeleteMapping("/product-posts/{productPostId}/options/{optionId}")
    public ResponseEntity<CommonResponse<Void>> optionDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productPostId, @PathVariable Long optionId) {

        postOptionService.optionDelete(authUser.getId(), authUser.getUserRole(), productPostId, optionId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("옵션 삭제 성공", null));
    }
}