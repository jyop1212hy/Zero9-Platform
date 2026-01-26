package com.zero9platform.domain.order.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.OrderStatus;
import com.zero9platform.domain.orderitem.entity.OrderItem;
import com.zero9platform.domain.orderitem.service.OrderItemService;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_item_id", unique = true)
    private OrderItem orderItem;

    @Column(nullable = false, unique = true)
    private String orderNo;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private String orderStatus;

    @Column
    private LocalDateTime canceledAt;

    public Order(OrderItem orderItem, String orderNo, Long totalAmount, String orderStatus) {
        this.orderItem = orderItem;
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;

        // 연관관계 양방향 세팅
        orderItem.setOrder(this);
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED.name();
        this.canceledAt = LocalDateTime.now();
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;

        if (orderItem != null && orderItem.getOrder() != this) {
            orderItem.setOrder(this); // 양방향 연관관계 유지
        }
    }

}
