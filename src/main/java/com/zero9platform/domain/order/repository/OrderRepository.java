package com.zero9platform.domain.order.repository;

import com.zero9platform.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndCanceledAtIsNull(Long orderId);

    Page<Order> findByOrderItem_User_IdAndCanceledAtIsNull(Long userId, Pageable pageable);
}
