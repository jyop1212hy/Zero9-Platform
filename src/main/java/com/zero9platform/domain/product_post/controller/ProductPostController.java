package com.zero9platform.domain.product_post.controller;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.comment.repository.CommentRepository;
import com.zero9platform.domain.product_post.model.request.ProductPostCreateRequest;
import com.zero9platform.domain.product_post.model.request.ProductPostUpdateRequest;
import com.zero9platform.domain.product_post.model.response.*;
import com.zero9platform.domain.product_post.service.ProductPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class ProductPostController {

    private final ProductPostService productPostService;
    private final CommentRepository commentRepository;

    /**
     * 상품 게시물 생성
     */
    @PostMapping("/product-posts")
    public ResponseEntity<CommonResponse<ProductPostCreateResponse>> productPostCreateHandler(@AuthenticationPrincipal AuthUser authUser, @RequestPart("ppCreateRequest") @Valid ProductPostCreateRequest request, @RequestPart(value = "contentImage", required = false) MultipartFile file) {

        Long userId = authUser.getId();

        ProductPostCreateResponse response = productPostService.productPostCreate(userId, request, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("상품 게시물 생성 성공", response));
    }

    /**
     * 상품 게시물 상세 조회
     */
    @GetMapping("/product-posts/{productPostId}")
    public ResponseEntity<CommonResponse<ProductPostGetDetailResponse>> productPostGetDetailHandler(@PathVariable Long productPostId) {

        ProductPostGetDetailResponse response = productPostService.productPostGetDetail(productPostId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 게시물 상세 조회 성공", response));
    }

    /**
     * 상품 게시물 목록 조회
     */
    @GetMapping("/product-posts")
    public ResponseEntity<CommonResponse<PageResponse<ProductPostGetListResponse>>> productPostGetListHandler(Pageable pageable) {

        PageResponse<ProductPostGetListResponse> response = PageResponse.from(productPostService.productPostGetList(pageable));

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 게시물 목록 조회 성공", response));
    }

    /**
     * 내가 등록한 판매 게시물 보기
     */
    @GetMapping("/product-posts/my")
    public ResponseEntity<CommonResponse<List<ProductPostGetMyListResponse>>> myProductPostGetLimitListHandler(@AuthenticationPrincipal AuthUser authUser, @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(0, limit);

        List<ProductPostGetMyListResponse> response = productPostService.myProductPostGetLimitList(authUser.getId(), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("내가 작성한 판매 상품 조회 완료", response));
    }

    /**
     * 상품 게시물 수정
     */
    @PatchMapping("/product-posts/{productPostId}")
    public ResponseEntity<CommonResponse<ProductPostUpdateResponse>> productPostUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productPostId, @RequestPart("ppUpdateRequest") ProductPostUpdateRequest request, @RequestPart(value = "contentImage", required = false) MultipartFile file) {

        Long userId = authUser.getId();

        ProductPostUpdateResponse response = productPostService.productPostUpdate(userId, productPostId, request, file);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("상품 게시물 수정 성공", response));
    }
}