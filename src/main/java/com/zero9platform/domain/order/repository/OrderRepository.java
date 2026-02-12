package com.zero9platform.domain.order.repository;

import com.zero9platform.domain.admin.model.response.order.OrderPaymentDetailResponse;
import com.zero9platform.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByOrderItem_User_Id(Long userId, Pageable pageable);

    /**
     * 주문 번호 조회
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 결제 대기 및 limitTime 이상인 Order 조회
     */
    @Query("""
        select o from Order o
        join fetch o.orderItem oi
        join fetch oi.productPostOption
        where o.orderStatus = :pending
        and o.createdAt <= :limitTime
    """)
    List<Order> findAllByOrderStatusAndCreatedAtBefore(@Param("pending")String pending, @Param("limitTime")LocalDateTime limitTime);

    /**
     * 관리자 결제 내역 조회
     */
    @Query("""
        SELECT new com.zero9platform.domain.admin.model.response.order.OrderPaymentDetailResponse(
            o.id,
            u.nickname,
            po.name,
            o.totalAmount,
            o.orderNo,
            p.paymentKey,
            o.orderStatus,
            o.canceledReason,
            o.createdAt,
            o.updatedAt
        )
        FROM Order o
        LEFT JOIN Payment p ON p.order = o
        JOIN o.orderItem ot
        JOIN ot.productPostOption po
        JOIN ot.user u
        WHERE o.orderStatus IN ('PAID', 'CANCELED') AND (:status IS NULL OR o.orderStatus = :status)
        ORDER BY o.updatedAt DESC
    """)
    List<OrderPaymentDetailResponse> findPaidOrderPaymentList(@Param("status") String status);
}