package com.zero9platform.domain.product_post.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ProductPostProgressStatus;
import com.zero9platform.common.enums.ProductPostStatus;
import com.zero9platform.domain.product.entity.Product;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "product_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer stock;

    @OneToMany(
            mappedBy = "productPost",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductPostOption> productPostOptionList = new ArrayList<>();

    @Column
    private String image;

    @Column(nullable = false)
    private String category = Category.ETC.name();

    // 판매 기간 기준 상태
    @Column(nullable = false)
    private String productPostProgressStatus = ProductPostProgressStatus.READY.name();

    // 판매 게시물 기준 상태 (옵션 / 노출)
    @Column(nullable = false)
    private String productPostStatus = ProductPostStatus.ACTIVE.name();

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private LocalDateTime deletedAt;

    public ProductPost(User user, Product product, String title, String content, Integer stock, String image, String category, String productPostProgressStatus, String productPostStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.product = product;
        this.title = title;
        this.content = content;
        this.stock = stock;
        this.image = image;
        this.category = category;
        this.productPostProgressStatus = productPostProgressStatus;
        this.productPostStatus = productPostStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void update(String title, String content, Integer stock, String image, String category, String productPostProgressStatus, LocalDateTime startDate, LocalDateTime endDate) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (stock != null) this.stock = stock;
        if (image != null) this.image = image;
        if (category != null) this.category = category;
        if (productPostProgressStatus != null) this.productPostProgressStatus = productPostProgressStatus;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;

    }

    public void addOption(ProductPostOption option) {
        productPostOptionList.add(option);
        option.setProductPost(this);
    }

    // 재고 증가
    public void increaseStock(Integer totalCapacity) {
        this.stock += totalCapacity;
    }

    // 재고 차감
    public void decreaseStock(Integer totalCapacity) {
        this.stock -= totalCapacity;
    }

    public void setProductPostStatus(String productPostStatus) {
        this.productPostStatus = productPostStatus;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }


    // 상품 게시물의 하위 옵션 리스트가 전부 "비활성화"인지 아닌지 검증
    public boolean allOptionsInactive() {

        for (ProductPostOption option: productPostOptionList) {
            if (ProductPostStatus.ACTIVE.name().equals(option.getOptionStatus())) {
                return false;
            }
        }

        this.productPostStatus = ProductPostStatus.INACTIVE.name();

        return true;
    }
}