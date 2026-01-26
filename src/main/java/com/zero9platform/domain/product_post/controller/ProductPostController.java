package com.zero9platform.domain.product_post.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post.model.request.ProductPostCreateRequest;
import com.zero9platform.domain.product_post.model.request.ProductPostUpdateRequest;
import com.zero9platform.domain.product_post.model.response.ProductPostCreateResponse;
import com.zero9platform.domain.product_post.model.response.ProductPostGetDetailResponse;
import com.zero9platform.domain.product_post.model.response.ProductPostUpdateResponse;
import com.zero9platform.domain.product_post.service.ProductPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class ProductPostController {

    private final ProductPostService productPostService;

    /**
     * 상품 게시물 생성
     */
    @PostMapping("/product/{productId}/product-posts")
    public ResponseEntity<CommonResponse<ProductPostCreateResponse>> productPostCreateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productId, @RequestBody ProductPostCreateRequest request) {
        Long userId = authUser.getId();

        ProductPostCreateResponse response = productPostService.productPostCreate(userId, productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("상품 게시물 생성 성공", response));
    }

    /**
     * 상품 게시물 상세 조회
     */
    @GetMapping("/product-posts/{productpostId}")
    public ResponseEntity<CommonResponse<ProductPostGetDetailResponse>> productPostGetDetailHandler(@PathVariable Long productpostId) {

        ProductPostGetDetailResponse response = productPostService.productPostGetDetail(productpostId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("상품 게시물 상세 조회 성공", response));
    }

    /**
     * 상품 게시물 목록 조회
     */
    @GetMapping("/product-posts")
    public ResponseEntity<CommonResponse<PageResponse<ProductPostGetDetailResponse>>> productPostGetListHandler(Pageable pageable) {

        PageResponse<ProductPostGetDetailResponse> responsePage = productPostService.productPostGetList(pageable);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("상품 게시물 목록 조회 성공", responsePage));
    }

    /**
     * 상품 게시물 수정
     */
    @PutMapping("/product-posts/{productpostId}")
    public ResponseEntity<CommonResponse<ProductPostUpdateResponse>> productPostUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productpostId, @RequestBody ProductPostUpdateRequest request) {

        Long userId = authUser.getId();

        ProductPostUpdateResponse response = productPostService.productPostUpdate(userId, productpostId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 게시물 수정 성공", response));
    }

    /**
     * 상품 게시물 삭제
     */
    @DeleteMapping("/product-posts/{productpostId}")
    public ResponseEntity<CommonResponse<Void>> productPostDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productpostId) {

        Long userId = authUser.getId();

        productPostService.productPostDelete(userId, productpostId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 게시물 삭제 성공", null));
    }
}
