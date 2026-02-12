package com.zero9platform.domain.orderitem.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.domain.order.entity.Order;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_id", nullable = false)
    private ProductPost productPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_option_id", nullable = false)
    private ProductPostOption productPostOption;

    @OneToOne(mappedBy = "orderItem", fetch = FetchType.LAZY)
    private Order order;

    @Column(nullable = false)
    private Integer orderQuantity;  // 수량

    public OrderItem(User user, ProductPost productPost, ProductPostOption productPostOption, Integer orderQuantity) {
        this.user = user;
        this.productPost = productPost;
        this.productPostOption = productPostOption;
        this.orderQuantity = orderQuantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}