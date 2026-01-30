package com.zero9platform.domain.product_post_option.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.StockStatus;
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
    private String name;    // 옵션명

    @Column(nullable = false)
    private Long salePrice; // 옵션가

    @Column(nullable = false)
    private Integer stockQuantity;  // 옵션 재고

    @Column(nullable = false)
    private String stockStatus; // 옵션 재고 상태

    public ProductPostOption(ProductPost productPost, String name, Long salePrice, Integer stockQuantity) {
        this.productPost = productPost;
        this.name = name;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        updateStockStatus();
    }

    public void update(ProductPost productPost, String name, Long salePrice, Integer stockQuantity) {

        if (productPost != null) this.productPost = productPost;
        if (name != null) this.name = name;
        if (salePrice != null) this.salePrice = salePrice;
        if (stockQuantity != null) this.stockQuantity = stockQuantity;
    }

    // 재고 상태 지정
    private void updateStockStatus() {
        if (this.stockQuantity != null && this.stockQuantity > 0) {
            this.stockStatus = StockStatus.IN_STOCK.name();
        } else {
            this.stockStatus = StockStatus.SOLD_OUT.name();
        }
    }

//    public void update(String name, Long salePrice, Integer capacity) {
//        if (name != null) this.name = name;
//        if (salePrice != null) this.salePrice = salePrice;
//        if (capacity != null) this.capacity = capacity;
//    }

    public void setProductPost(ProductPost productPost) {
        this.productPost = productPost;
    }

    public void optionSoldOut() {
        this.stockStatus = StockStatus.SOLD_OUT.name();
    }
}