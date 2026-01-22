package com.zero9platform.domain.product_post_option.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionUpdateRequest;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionGetDetailResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionGetListResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionUpdateResponse;
import com.zero9platform.domain.product_post_option.service.ProductPostOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/product-posts")
public class ProductPostOptionController {

    private final ProductPostOptionService postOptionService;

    /**
     * 옵션 생성
     */
    @PostMapping("/{productPostId}/options")
    public ResponseEntity<CommonResponse<ProductPostOptionCreateResponse>> optionCreateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productPostId, @RequestBody @Valid ProductPostOptionCreateRequest request) {

        ProductPostOptionCreateResponse response = postOptionService.optionCreate(authUser.getId(), authUser.getUserRole(), productPostId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("옵션 생성 성공", response));
    }

    /**
     * 옵션 상세 조회
     */
    @GetMapping("/{productPostId}/options/{optionId}")
    public ResponseEntity<CommonResponse<ProductPostOptionGetDetailResponse>> optionGetHandler(@PathVariable Long productPostId, @PathVariable Long optionId) {
        ProductPostOptionGetDetailResponse response = postOptionService.optionGetDetail(productPostId, optionId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("옵션 상세 조회 성공", response));
    }

    /**
     *  상품 게시물별 옵션 전체 목록 조회
     */
    @GetMapping("/{productPostId}/options")
    public ResponseEntity<CommonResponse<PageResponse<ProductPostOptionGetListResponse>>> optionGetListHandler(@PathVariable Long productPostId, Pageable pageable) {

        PageResponse<ProductPostOptionGetListResponse> response = postOptionService.optionGetPage(productPostId, pageable);

        return ResponseEntity.ok(CommonResponse.success("상품 게시물별 옵션 전체 목록 조회 성공", response));
    }

    /**
     * 옵션 수정
     */
    @PutMapping("/{productPostId}/options/{optionId}")
    public ResponseEntity<CommonResponse<ProductPostOptionUpdateResponse>> optionUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productPostId, @PathVariable Long optionId, @Valid @RequestBody ProductPostOptionUpdateRequest request) {

        ProductPostOptionUpdateResponse response = postOptionService.optionUpdate(authUser.getId(), authUser.getUserRole(), productPostId, optionId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("옵션 수정 성공", response));
    }

    /**
     * 옵션 삭제
     * */
    @DeleteMapping("/{productPostId}/options/{optionId}")
    public ResponseEntity<CommonResponse<Void>> optionDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productPostId, @PathVariable Long optionId) {

        postOptionService.optionDelete(authUser.getId(), authUser.getUserRole(), productPostId, optionId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("옵션 삭제 성공", null));
    }
}