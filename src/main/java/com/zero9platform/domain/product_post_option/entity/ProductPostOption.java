package com.zero9platform.domain.product_post_option.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.domain.product_post.entity.ProductPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_post_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPostOption  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_id", nullable = false)
    private ProductPost productPost;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long optionPrice;

    @Column(nullable = false)
    private Integer capacity;

    public ProductPostOption(ProductPost productPost, String name, Long optionPrice, Integer capacity) {
        this.productPost = productPost;
        this.name = name;
        this.optionPrice = optionPrice;
        this.capacity = capacity;
    }

    public void update(String name, Long price, Integer capacity) {
        if (name != null) this.name = name;
        if (price != null) this.optionPrice = price;
        if (capacity != null) this.capacity = capacity;
    }

    public void setProductPost(ProductPost productPost) {
        this.productPost = productPost;
    }
}