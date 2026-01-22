package com.zero9platform.domain.product_post.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.PppProgressStatus;
import com.zero9platform.domain.product.entity.Product;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    // 옵션 컬럼 추가 예정

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long stock;

    @Column
    private String image;

    // Enum 값을 DB에 "문자열 이름 그대로" 저장
    @Enumerated(EnumType.STRING)
    private Category category = Category.ETC;

    @Enumerated(EnumType.STRING)
    private PppProgressStatus pppProgressStatus = PppProgressStatus.READY;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private LocalDateTime deletedAt;

    public ProductPost(String title, String content, Long stock, String image, Category category, PppProgressStatus pppProgressStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.content = content;
        this.stock = stock;
        this.image = image;
        this.category = category;
        this.pppProgressStatus = pppProgressStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}