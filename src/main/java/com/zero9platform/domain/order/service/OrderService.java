package com.zero9platform.domain.order.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.OrderStatus;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.util.OrderCodeGenerator;
import com.zero9platform.common.util.payment.toss.TossPaymentClient;
import com.zero9platform.domain.order.entity.Order;
import com.zero9platform.domain.order.model.request.OrderPaymentRequest;
import com.zero9platform.domain.order.model.response.OrderCancelResponse;
import com.zero9platform.domain.order.model.response.OrderCreateResponse;
import com.zero9platform.domain.order.model.response.OrderGetDetailResponse;
import com.zero9platform.domain.order.repository.OrderRepository;
import com.zero9platform.domain.orderitem.entity.OrderItem;
import com.zero9platform.domain.orderitem.repository.OrderItemRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductPostOptionRepository productPostOptionRepository;
    private final TossPaymentClient tossPaymentClient;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderCreateResponse orderCreate(Long userId, Long orderItemId) {

        // 본인 인증 (orderItem의 userId만 주문 생성 가능)
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDERITEM_NOT_FOUND));

        if (!Objects.equals(userId, orderItem.getUser().getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        if (orderItem.getOrder() != null) {
            throw new CustomException(ExceptionCode.ALREADY_ORDERED);
        }

        // 총 결제 금액
        ProductPostOption option = orderItem.getProductPostOption();
        Long salePrice = option.getSalePrice(); // 옵션가
        Integer stockQuantity = option.getStockQuantity();    // 재고 수량
        Integer orderQuantity = orderItem.getOrderQuantity(); // 주문 수량
        Long totalAmount = salePrice * orderQuantity; // 총 결제 금액

        // 재고 검증
        // 재고보다 선택한 수량이 많을 때 예외 처리 및 남아있는 재고 반환
//        if (orderQuantity > stockQuantity) {
//            throw new CustomException(ExceptionCode.INSUFFICIENT_STOCK, stockQuantity);
//        }

        // 결제 상태 변경
        // 현재는 페이먼츠와 연동 전이므로 "결제 완료"로 상태로 처리
        String orderStatus = OrderStatus.PENDING.name();

        // 재고 차감
        option.decreaseStock(orderQuantity);

//        if (productPost.getStock() == 0) {
//            // 품절 = 재고가 0개
//            activityFeedService.feedCreate("SOLD_OUT", productPost.getId(), productPost.getTitle());
//        } else if (productPost.getStock() <= 10) {
//            // 재고가 10개 이하
//            activityFeedService.feedCreate("LOW_STOCK", productPost.getId(), productPost.getTitle());
//        }

        // 주문 고유번호 생성
        String orderNo = OrderCodeGenerator.generate();

        // 주문 생성
        Order order = new Order(orderItem, orderNo, totalAmount, orderStatus);

        Order savedOrder = orderRepository.save(order);

        // 피드 생성
        //activityFeedService.feedCreate("PAYMENT", savedOrder.getOrderItem().getProductPost().getId(), savedOrder.getOrderItem().getProductPost().getTitle());

        return OrderCreateResponse.from(savedOrder);
    }

    /**
     * 주문 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderGetDetailResponse orderGetDetail(Long userId, Long orderId) {

        // 주문 권한 체크
        Order order = checkOrderPermission(orderRepository.findById(orderId), userId);

        return OrderGetDetailResponse.from(order);
    }

    /**
     * 사용자의 주문 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<OrderGetDetailResponse> orderGetList(Long userId, Pageable pageable) {

        Page<Order> orderPage = orderRepository.findByOrderItem_User_Id(userId, pageable);

        Page<OrderGetDetailResponse> OrderGetDetailResponsePage = orderPage.map(OrderGetDetailResponse::from);

        return OrderGetDetailResponsePage;
    }

    /**
     * 결제 완료
     */
    @Transactional
    public void orderPayment(Long userId, Long orderId, OrderPaymentRequest request) {

        // 주문 권한 체크
        Order order = checkOrderPermission(orderRepository.findById(orderId), userId);

        // 주문 조회
        orderRepository.findByOrderNo(request.getOrderNo())
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDER_NOT_FOUND));

        // 결제 금액 검증
        if (request.getAmount() != order.getTotalAmount()) {
            throw new CustomException(ExceptionCode.ORDER_AMOUNT_MISMATCH);
        }

        // TossPayments 결제 승인
        tossPaymentClient.tossPayment(
                request.getPaymentKey(),
                request.getOrderNo(),
                request.getAmount()
        );

        order.paymentStatusUpdate(OrderStatus.PAID);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public OrderCancelResponse orderCancel(Long userId, Long orderId) {

        // 주문 권한 체크
        Order order = checkOrderPermission(orderRepository.findById(orderId), userId);

        // 결제 상태 변경
        OrderStatus.CANCELED.name();

        // 재고 증가
        OrderItem orderItem = orderItemRepository.findById(order.getOrderItem().getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDERITEM_NOT_FOUND));

        Integer orderQuantity = orderItem.getOrderQuantity(); // 구매 수량
        Long optionId = orderItem.getProductPostOption().getId();

        ProductPostOption option = productPostOptionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ExceptionCode.OPTION_NOT_FOUND));

        option.increaseStock(orderQuantity);

        order.cancel();

        return OrderCancelResponse.from(order);
    }

    /**
     * 주문 권한 체크
     */
    private Order checkOrderPermission(Optional<Order> orderRepository, Long userId) {
        Order order = orderRepository
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDER_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 본인 인증
        if (!Objects.equals(order.getOrderItem().getUser().getId(), user.getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
        return order;
    }
}
