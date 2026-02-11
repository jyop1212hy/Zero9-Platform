package com.zero9platform.domain.orderitem.service;

import com.zero9platform.common.enums.*;
//import com.zero9platform.common.enums.DisplayStatus;
import com.zero9platform.common.exception.CustomException;
//import com.zero9platform.domain.order.entity.Order;
import com.zero9platform.domain.order.entity.Order;
import com.zero9platform.domain.orderitem.entity.OrderItem;
import com.zero9platform.domain.orderitem.model.request.OrderItemCreateRequest;
import com.zero9platform.domain.orderitem.model.response.OrderItemCreateResponse;
import com.zero9platform.domain.orderitem.model.response.OrderItemGetDetailResponse;
import com.zero9platform.domain.orderitem.repository.OrderItemRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.InfluencerRepository;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final UserRepository userRepository;
    private final InfluencerRepository influencerRepository;
    private final ProductPostRepository productPostRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 주문 상품 생성
     */
    @Transactional
    public OrderItemCreateResponse orderItemCreate(Long userId, Long productPostId, OrderItemCreateRequest request) {

        // 회원 조회 - 탈퇴한 회원 제외
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 상품 게시물 조회
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 상품판매 게시물이 "DOING"일 때만 주문 상품 생성 가능
        if (!productPost.getProgressStatus().equals(ProgressStatus.DOING.name())) {
            throw new CustomException(ExceptionCode.SALE_NOT_IN_PROGRESS);
        }

        // 옵션 (옵션명, 수량)
        Long optionId = request.getOptionId();
        List<ProductPostOption> productPostOptionList = productPost.getProductPostOptionList();
        ProductPostOption option = productPostOptionList.stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionCode.OPTION_NOT_FOUND));

        // 해당 옵션이 품절일 시, 주문 상품 생성 불가
        if (option.getStockStatus().equals(StockStatus.SOLD_OUT.name())) {
            throw new CustomException(ExceptionCode.OPTION_SOLD_OUT);
        }

        // 재고보다 많은 수량 선택할 시, 예외 처리
        if (request.getOrderQuantity() > option.getStockQuantity()) {
            throw new CustomException(ExceptionCode.INSUFFICIENT_STOCK, option.getStockQuantity());
        }

        OrderItem orderItem = new OrderItem(user, productPost, option, request.getOrderQuantity());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        return OrderItemCreateResponse.from(savedOrderItem);
    }

    /**
     * 주문 상품 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderItemGetDetailResponse orderItemGetDetail(Long userId, Long orderItemId) {

        // 회원 조회
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 사용자 권한 체크
        checkUserPermission(user, userId);

        // 주문 상품 권한 체크
        OrderItem orderItem = checkOrderItemPermission(orderItemId, user);

        return OrderItemGetDetailResponse.from(orderItem);
    }

    /**
     * 주문 상품 삭제
     */
    @Transactional
    public void orderItemDelete(Long userId, Long orderItemId) {

        // 회원 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 사용자 권한 체크
        checkUserPermission(user, userId);

        // 주문 상품 권한 체크
        OrderItem orderItem = checkOrderItemPermission(orderItemId, user);

        // 결제 완료 & 취소 상태 -> 주문 상품 삭제 불가
        Order order = orderItem.getOrder();

        if (order != null) {

            // 결제 완료된 주문이면 주문 상품 삭제 불가
            if (OrderStatus.PAID.name().equals(order.getOrderStatus()) || OrderStatus.CANCELED.name().equals(order.getOrderStatus())) {
                throw new CustomException(ExceptionCode.ALREADY_ORDERED_ORDERITEM);
            }

            // PENDING 또는 CANCELED라면 삭제 가능, 연관 끊기
            order.setOrderItem(null);  // 양방향 관계 끊기
        }

        // 결제 대기 상태인 주문이면 주문 상품 삭제 가능
        orderItemRepository.delete(orderItem);
    }


    /**
     * 회원 권한 체크
     */
    private void checkUserPermission(User user, Long targetUserId) {

        // 관리자는 모든 대상 가능
        if (user.getRole().equals(UserRole.ADMIN.name())) {
            return; // 권한 OK
        }

        // 일반 회원은 본인만 가능
        if (!user.getId().equals(targetUserId)) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }

    /**
     * 주문 상품 권한 체크
     */
    private OrderItem checkOrderItemPermission(Long orderItemId, User user) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDERITEM_NOT_FOUND));

        if (!Objects.equals(user.getId(), orderItem.getUser().getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        return orderItem;
    }
}
