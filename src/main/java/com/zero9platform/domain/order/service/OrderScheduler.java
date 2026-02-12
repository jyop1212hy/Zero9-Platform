package com.zero9platform.domain.order.service;

import com.zero9platform.domain.order.entity.Order;
import com.zero9platform.domain.order.repository.OrderRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateOrderStatus() {

        LocalDateTime limitTime = LocalDateTime.now().minusMinutes(30);

        // 주문 생성 후, 30분 이내 미결제 주문들 반환
        List<Order> orderList = orderRepository.findAllByOrderStatusAndCreatedAtBefore("PENDING", limitTime);

        for (Order order: orderList) {
            try {

                // 주문 상태를 "결제 취소"로 변경
                order.cancel();

                // 재고 복구
                ProductPostOption option = order.getOrderItem().getProductPostOption();
                option.increaseStock(order.getOrderItem().getOrderQuantity());

                log.info("[자동취소 성공] 주문 ID: {} | 복구 재고: {}개", order.getId(), order.getOrderItem().getOrderQuantity());
            } catch (Exception e) {
                log.info("[자동취소 실패] 주문 ID: {} | 사유: {}", order.getId(), e.getMessage());
            }
        }
    }
}