package com.zero9platform.domain.product_post_favorite.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post_favorite.service.ProductPostFavoriteService;
import com.zero9platform.domain.product_post_favorite.model.response.ProductPostFavoriteCreateResponse;
import com.zero9platform.domain.product_post_favorite.model.response.ProductPostFavoriteGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class ProductPostFavoriteController {

    private final ProductPostFavoriteService productPostFavoriteService;

    /**
     * 찜 등록
     */
    @PostMapping("/product-posts/{productPostId}/favorites")
    public ResponseEntity<CommonResponse<ProductPostFavoriteCreateResponse>> favoriteCreateHandler(@PathVariable Long productPostId, @AuthenticationPrincipal AuthUser authUser) {

        ProductPostFavoriteCreateResponse createResponse = productPostFavoriteService.favoriteCreate(productPostId, authUser);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("찜 등록 성공", createResponse));
    }

    /**
     * 찜 등록 취소
     */
    @DeleteMapping("/product-posts/{productPostId}/favorites")
    public ResponseEntity<CommonResponse<Void>> favoriteCancellationHandler(@PathVariable Long productPostId, @AuthenticationPrincipal AuthUser authUser) {

        productPostFavoriteService.favoriteCancellation(productPostId, authUser);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("찜 취소 성공", null));
    }

    /**
     * 찜 목록 조회
     */
    @GetMapping("/favorites")
    public ResponseEntity<CommonResponse<PageResponse<ProductPostFavoriteGetResponse>>> favoriteGetPageHandler(@AuthenticationPrincipal AuthUser authUser, Pageable pageable) {

        Page<ProductPostFavoriteGetResponse> favoriteList = productPostFavoriteService.favoriteGetPage(authUser, pageable);

        PageResponse<ProductPostFavoriteGetResponse> pageResponse = PageResponse.from(favoriteList);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("찜 목록 조회 성공", pageResponse));
    }
}