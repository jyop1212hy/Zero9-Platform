package com.zero9platform.domain.product_post.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.ProgressStatus;
//import com.zero9platform.common.enums.DisplayStatus;
import com.zero9platform.common.exception.CustomException;
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
    private Long id;    // 상품 게시물 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 회원 ID

    @Column(nullable = false)
    private String title;   // 제목

    @JoinColumn(nullable = false)
    private String name; // 상품명

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @Column(nullable = false)
    private Long originalPrice;     // 정가

    @OneToMany(
            mappedBy = "productPost",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductPostOption> productPostOptionList = new ArrayList<>();

    @Column
    private String image;   // 이미지

    @Column(nullable = false)
    private String category = Category.ETC.name();  // 카테고리

    @Column(nullable = false)
    private String progressStatus = ProgressStatus.READY.name();    // 판매 기간 상태

//    @Column(nullable = false)
//    private String saleStatus = DisplayStatus.ACTIVE.name();    // 판매 가능 상태

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    public ProductPost(User user, String title, String name, String content, Long originalPrice, String image, String category, String progressStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.title = title;
        this.name = name;
        this.content = content;
        this.originalPrice = originalPrice;
        this.image = image;
        this.category = category;
        this.progressStatus = progressStatus;

        if (startDate == null || endDate == null) {
            throw new CustomException(ExceptionCode.PP_DATE_REQUIRED);
        }

        if (endDate.isBefore(startDate)) {
            throw new CustomException(ExceptionCode.PP_INVALID_DATE_RANGE);
        }

        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void update(String category, String progressStatus, String title, String name, String content, Long originalPrice, String image, LocalDateTime startDate, LocalDateTime endDate) {
        if (category != null) this.category = category;
        if (progressStatus != null) this.progressStatus = progressStatus;
        if (title != null) this.title = title;
        if (name != null) this.name = name;
        if (content != null) this.content = content;
        if (originalPrice != null) this.originalPrice = originalPrice;
        if (image != null) this.image = image;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
    }

    public void addOption(ProductPostOption option) {
        productPostOptionList.add(option);
        option.setProductPost(this);
    }
}