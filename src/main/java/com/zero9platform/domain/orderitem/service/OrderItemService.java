package com.zero9platform.domain.orderitem.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.admin.repository.InfluencerRepository;
import com.zero9platform.domain.orderitem.entity.OrderItem;
import com.zero9platform.domain.orderitem.model.request.OrderItemCreateRequest;
import com.zero9platform.domain.orderitem.model.response.OrderItemCreateResponse;
import com.zero9platform.domain.orderitem.model.response.OrderItemGetDetailResponse;
import com.zero9platform.domain.orderitem.repository.OrderItemRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.user.entity.User;
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
    public OrderItemCreateResponse orderItemCreate(Long userId, Long productpostId, OrderItemCreateRequest request) {

        // 회원 조회 (관리자 인가 제외)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER));

        if (user.getRole() == UserRole.ADMIN.name()) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        // 상품 게시물 조회
        ProductPost productPost = productPostRepository.findById(productpostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 옵션 (옵션명, 수량)
        Long optionId = request.getOptionId();
        List<ProductPostOption> productPostOptionList = productPost.getProductPostOptionList();
        ProductPostOption option = productPostOptionList.stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionCode.OPTION_NOT_FOUND));

        OrderItem orderItem = new OrderItem(user, productPost, option, request.getQuantity());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        return OrderItemCreateResponse.from(savedOrderItem);
    }

    /**
     * 주문 상품 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderItemGetDetailResponse orderItemGetDetail(Long userId, Long orderItemId) {

        // 본인 인증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDERITEM_NOT_FOUND));

        if (!Objects.equals(user.getId(), orderItem.getUser().getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        return OrderItemGetDetailResponse.from(orderItem);
    }

    /**
     * 주문 상품 삭제
     */
    @Transactional
    public void orderItemDelete(Long userId, Long orderItemId) {

        // 본인 인증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ORDERITEM_NOT_FOUND));

        if (!Objects.equals(user.getId(), orderItem.getUser().getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        orderItemRepository.deleteById(orderItemId);
    }
}
