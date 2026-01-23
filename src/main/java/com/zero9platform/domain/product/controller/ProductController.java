package com.zero9platform.domain.product.controller;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product.model.request.ProductCreateRequest;
import com.zero9platform.domain.product.model.request.ProductUpdateRequest;
import com.zero9platform.domain.product.model.response.ProductCreateResponse;
import com.zero9platform.domain.product.model.response.ProductGetDetailResponse;
import com.zero9platform.domain.product.model.response.ProductUpdateResponse;
import com.zero9platform.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/zero9/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 생성
     */
    @PostMapping
    public ResponseEntity<CommonResponse<ProductCreateResponse>> productCreateHandler(@AuthenticationPrincipal AuthUser authUser, @RequestBody ProductCreateRequest request) {

        Long userId = authUser.getId();

        ProductCreateResponse response = productService.productCreate(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("상품 생성 성공", response));
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductGetDetailResponse>> productGetDetailHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productId) {

        Long userId = authUser.getId();

        ProductGetDetailResponse response = productService.productGetDetail(userId, productId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 상세 조회 성공", response));
    }

    /**
     * 상품 목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<ProductGetDetailResponse>>> productGetListHandler(@AuthenticationPrincipal AuthUser authUser, Pageable pageable) {

        Long userId = authUser.getId();

        PageResponse<ProductGetDetailResponse> response = productService.productGetList(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 목록 조회 성공", response));
    }

    /**
     * 상품 수정
     */
    @PutMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductUpdateResponse>> productUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productId, @RequestBody ProductUpdateRequest request) {

        Long userId = authUser.getId();

        ProductUpdateResponse response = productService.productUpdate(userId, productId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 수정 성공", response));
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonResponse<Void>> productDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productId) {

        Long userId = authUser.getId();

        productService.productDelete(userId, productId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 삭제 성공", null));
    }

}
