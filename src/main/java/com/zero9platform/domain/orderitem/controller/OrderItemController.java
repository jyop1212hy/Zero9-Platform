package com.zero9platform.domain.orderitem.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.comment.entity.Comment;
import com.zero9platform.domain.orderitem.model.request.OrderItemCreateRequest;
import com.zero9platform.domain.orderitem.model.response.OrderItemCreateResponse;
import com.zero9platform.domain.orderitem.model.response.OrderItemGetDetailResponse;
import com.zero9platform.domain.orderitem.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * 주문 상품 생성
     */
    @PostMapping("/zero9/product-posts/{productPostId}/order-items")
    public ResponseEntity<CommonResponse<OrderItemCreateResponse>> orderItemCreateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long productPostId, @RequestBody OrderItemCreateRequest request) {

        Long userId = authUser.getId();

        OrderItemCreateResponse response = orderItemService.orderItemCreate(userId, productPostId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("주문 상품 생성 성공", response));
    }

    /**
     * 주문 상품 상세 조회
     */
    @GetMapping("/zero9/order-items/{orderItemId}")
    public ResponseEntity<CommonResponse<OrderItemGetDetailResponse>> orderItemGetDetailHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long orderItemId) {

        Long userId = authUser.getId();

        OrderItemGetDetailResponse response = orderItemService.orderItemGetDetail(userId, orderItemId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("주문 상품 상세 조회 성공", response));
    }

    /**
     * 주문 상품 삭제
     */
    @DeleteMapping("/zero9/order-items/{orderItemId}")
    public ResponseEntity<CommonResponse<Void>> orderItemDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long orderItemId) {

        Long userId = authUser.getId();

        orderItemService.orderItemDelete(userId, orderItemId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("주문 상품 삭제 성공", null));
    }
}
