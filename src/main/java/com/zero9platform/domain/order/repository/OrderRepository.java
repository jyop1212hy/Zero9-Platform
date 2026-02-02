package com.zero9platform.domain.order.repository;

import com.zero9platform.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByOrderItem_User_Id(Long userId, Pageable pageable);

    /**
     * 주문 번호 조회
     */
    Optional<Order> findByOrderNo(String orderNo);
}
