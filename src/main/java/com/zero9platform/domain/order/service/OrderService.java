package com.zero9platform.domain.order.service;

import com.zero9platform.common.enums.*;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.FeedType;
import com.zero9platform.common.enums.OrderStatus;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.util.OrderCodeGenerator;
import com.zero9platform.common.util.payment.toss.TossPaymentClient;
import com.zero9platform.domain.activity_feed.event.FeedCreateEvent;
import com.zero9platform.domain.order.entity.Order;
import com.zero9platform.domain.order.entity.Payment;
import com.zero9platform.domain.order.model.request.OrderPaymentCancelReasonRequest;
import com.zero9platform.domain.order.model.request.OrderPaymentRequest;
import com.zero9platform.domain.order.model.response.OrderCancelResponse;
import com.zero9platform.domain.order.model.response.OrderCreateResponse;
import com.zero9platform.domain.order.model.response.OrderGetDetailResponse;
import com.zero9platform.domain.order.repository.OrderRepository;
import com.zero9platform.domain.order.repository.PaymentRepository;
import com.zero9platform.domain.orderitem.entity.OrderItem;
import com.zero9platform.domain.orderitem.repository.OrderItemRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderCreateResponse orderCreate(Long userId, Long orderItemId) {

        // 본인 인증 (orderItem의 userId만 주문 생성 가능)
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDER_ITEM_NOT_FOUND));

        ProductPost productPost = orderItem.getProductPost();

        // 상품판매 게시물이 "DOING"일 때만 주문 생성 가능
        if (!ProgressStatus.DOING.name().equals(productPost.getProgressStatus())) {
            throw new CustomException(ExceptionCode.PRODUCT_POST_NOT_IN_PROGRESS);
        }

        // 본인의 주문 상품이 맞는지 검증
        if (!Objects.equals(userId, orderItem.getUser().getId())) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        // 이미 주문한 상품이라면 예외처리
        if (orderItem.getOrder() != null) {
            throw new CustomException(ExceptionCode.ORDER_ALREADY_COMPLETED);
        }

        // 총 결제 금액
        Long optionId = orderItem.getProductPostOption().getId();

        // 비관락으로 재고 조회
        ProductPostOption option = productPostOptionRepository.findByIdWithLock(optionId)
                .orElseThrow(() -> new CustomException(ExceptionCode.OPTION_NOT_FOUND));

        Long salePrice = option.getSalePrice(); // 옵션가
        Integer orderQuantity = orderItem.getOrderQuantity(); // 주문 수량
        Long totalAmount = salePrice * orderQuantity; // 총 결제 금액

        // 재고 차감
        option.decreaseStock(orderQuantity);

        Long productId = productPost.getId();
        String title = productPost.getTitle();

        // 피드 생성
        // 1. 품절 완료 피드 (SOLD_OUT)
        if (option.getStockQuantity() == 0) {
            eventPublisher.publishEvent(new FeedCreateEvent(FeedType.SOLD_OUT, productId, title, null));
        }

        // 2. 품절 임박 피드 (LOW_STOCK) - 기준: 5개 이하일 때
        else if (option.getStockQuantity() <= 5) {
            eventPublisher.publishEvent(new FeedCreateEvent(FeedType.LOW_STOCK, productId, title, null));
        }

        // 결제 상태 변경
        String orderStatus = OrderStatus.PENDING.name();

        // 주문 고유번호 생성
        String orderNo = OrderCodeGenerator.generate();

        // 주문 생성
        Order order = new Order(orderItem, orderNo, totalAmount, orderStatus);

        Order savedOrder = orderRepository.save(order);

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
        tossPaymentClient.tossPayment(request.getPaymentKey(), request.getOrderNo(), request.getAmount());

        order.paymentStatusUpdate(OrderStatus.PAID);

        Payment payment = new Payment(order, request.getPaymentKey());

        // 피드 생성을 위한 데이터 준비
        Long productId = order.getOrderItem().getProductPost().getId();
        String title = order.getOrderItem().getProductPost().getTitle();

        // 1. 전체 피드용 (누구나 보는 'N명 주문 중') -> userId를 null로 던짐
        eventPublisher.publishEvent(new FeedCreateEvent(FeedType.PAYMENT_COUNT, productId, title, null));

        // 2. 개인 피드용 (나만 보는 '내 주문 완료') -> userId를 그대로 던짐
        eventPublisher.publishEvent(new FeedCreateEvent(FeedType.PAYMENT_USER, productId, title, userId));

        paymentRepository.save(payment);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public OrderCancelResponse orderCancel(Long userId, Long orderId, OrderPaymentCancelReasonRequest request) {

        // 주문 권한 체크
        Order order = checkOrderPermission(orderRepository.findById(orderId), userId);

        // 이미 취소된 주문일 시, 예외 처리
        if (order.getOrderStatus().equals(OrderStatus.CANCELED.name())) {
            throw new CustomException(ExceptionCode.ORDER_ALREADY_CANCELLED);
        }

        OrderItem orderItem = orderItemRepository.findById(order.getOrderItem().getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDER_ITEM_NOT_FOUND));

        Integer orderQuantity = orderItem.getOrderQuantity(); // 구매 수량
        Long optionId = orderItem.getProductPostOption().getId();

        // 재고 복구
        ProductPostOption option = productPostOptionRepository.findByIdWithLock(optionId)
                .orElseThrow(() -> new CustomException(ExceptionCode.OPTION_NOT_FOUND));

        option.increaseStock(orderQuantity);

        // 결제 취소 (상태 변경)
        order.cancel();

        // "결제 완료" 상태일 때 결제 키 확인
        if (OrderStatus.PAID.name().equals(order.getOrderStatus())) {

            // 결제 키 찾을 수 없음
            Payment payment = paymentRepository.findPaymentKeyByOrder_Id(orderId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.TOSS_PAYMENT_KEY_NOT_FOUND));

            tossPaymentClient.cancelPayment(payment.getPaymentKey(), request.getCanceledReason());

            order.canceledReasonUpdate(request.getCanceledReason());
        }

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

        // 권한 체크 (관리자면 즉시 통과)
        if (user.getRole().equals(UserRole.ADMIN.name())) {
            return order;
        }

        // 본인 여부 확인
        if (!Objects.equals(order.getOrderItem().getUser().getId(), user.getId())) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        return order;
    }
}