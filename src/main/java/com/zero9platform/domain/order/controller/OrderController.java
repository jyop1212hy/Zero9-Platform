package com.zero9platform.domain.order.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.order.model.request.OrderPaymentRequest;
import com.zero9platform.domain.order.model.response.OrderCancelResponse;
import com.zero9platform.domain.order.model.response.OrderCreateResponse;
import com.zero9platform.domain.order.model.response.OrderGetDetailResponse;
import com.zero9platform.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     */
    @PostMapping("/zero9/order-item/{orderItemId}/orders")
    public ResponseEntity<CommonResponse<OrderCreateResponse>> orderCreateHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long orderItemId) {

        Long userId = authUser.getId();

        OrderCreateResponse response = orderService.orderCreate(userId, orderItemId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("주문 생성 성공", response));
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/zero9/orders/{orderId}")
    public ResponseEntity<CommonResponse<OrderGetDetailResponse>> orderGetDetailHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long orderId) {

        Long userId = authUser.getId();

        OrderGetDetailResponse response = orderService.orderGetDetail(userId, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("주문 상세 조회 성공", response));
    }

    /**
     * 사용자의 주문 목록 조회
     */
    @GetMapping("/zero9/orders")
    public ResponseEntity<CommonResponse<PageResponse<OrderGetDetailResponse>>> orderGetListHandler(@AuthenticationPrincipal AuthUser authUser, Pageable pageable) {

        Long userId = authUser.getId();

        Page<OrderGetDetailResponse> page = orderService.orderGetList(userId, pageable);

        PageResponse<OrderGetDetailResponse> response = PageResponse.from(page);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("사용자의 주문 목록 조회 성공", response));
    }

    /**
     * 결제 완료
     */
    @PutMapping("/zero9/orders/{orderId}")
    public ResponseEntity<CommonResponse<Void>> orderPaymentHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long orderId, @RequestBody OrderPaymentRequest request) {

        Long userId = authUser.getId();

        orderService.orderPayment(userId, orderId, request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("결제가 완료 되었습니다.", null));
    }

    /**
     * 주문 취소
     */
    @PatchMapping("/zero9/orders/{orderId}")
    public ResponseEntity<CommonResponse<OrderCancelResponse>> orderCancelHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long orderId) {

        Long userId = authUser.getId();

        OrderCancelResponse response = orderService.orderCancel(userId, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("주문 취소 성공", response));
    }
}
