package com.zero9platform.domain.product_post.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.ProgressStatus;
import com.zero9platform.common.enums.SaleStatus;
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

    @Column(nullable = false)
    private String saleStatus = SaleStatus.ACTIVE.name();    // 판매 가능 상태

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    public ProductPost(User user, String title, String name, String content, Long originalPrice, String image, String category, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.title = title;
        this.name = name;
        this.content = content;
        this.originalPrice = originalPrice;
        this.image = image;
        this.category = category;

        if (startDate == null || endDate == null) {
            throw new CustomException(ExceptionCode.PP_DATE_REQUIRED);
        }

        if (endDate.isBefore(startDate)) {
            throw new CustomException(ExceptionCode.PP_INVALID_DATE_RANGE);
        }

        this.startDate = startDate;
        this.endDate = endDate;
        updateProgressStatus();
    }

    public void update(String category, String title, String name, String content, Long originalPrice, String image, LocalDateTime startDate, LocalDateTime endDate) {
        if (category != null) this.category = category;
        if (title != null) this.title = title;
        if (name != null) this.name = name;
        if (content != null) this.content = content;
        if (originalPrice != null) this.originalPrice = originalPrice;
        if (image != null) this.image = image;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
    }

    // 판매 기간 상태
    private void updateProgressStatus() {

        // 준비, 진행, 종료
        // 준비: 현재 시각 < 시작일
        // 진행: 시작일 < 현재 시각 && 현재 시각 < 종료일
        // 종료: 종료일 < 현재 시각
        if (this.startDate.isAfter(LocalDateTime.now())) {
            this.progressStatus = ProgressStatus.READY.name();
        } else if (this.startDate.isBefore(LocalDateTime.now()) && this.endDate.isAfter(LocalDateTime.now())) {
            this.progressStatus = ProgressStatus.DOING.name();
        } else if (this.endDate.isBefore(LocalDateTime.now())) {
            this.progressStatus = ProgressStatus.END.name();
        }
    }

    // 판매 가능 상태
    private void updateSaleStatus() {
        // optionList의 모든 option의 stockStatus가 SOLD_OUT이면 SOLD_OUT 상태
        // option
    }

    public void addOption(ProductPostOption option) {
        productPostOptionList.add(option);
        option.setProductPost(this);
    }

//    public void setProductPostStatus(String productPostStatus) {
//        this.productPostStatus = productPostStatus;
//    }

    // 상품 게시물의 하위 옵션 리스트가 전부 "비활성화"인지 아닌지 검증
//    public boolean allOptionsInactive() {
//
//        for (ProductPostOption option: productPostOptionList) {
//            if (SaleStatus.ACTIVE.name().equals(option.getOptionStatus())) {
//                return false;
//            }
//        }
//
//        this.productPostStatus = SaleStatus.INACTIVE.name();
//
//        return true;
//    }

    public void allOptionIsEmpty() {

        // 옵션이 비었을 때 판매 상태 "비활성화" -> 추후 주문 상품 생성 불가 해야 함
        if (productPostOptionList.isEmpty()) {
            this.saleStatus = SaleStatus.INACTIVE.name();
        }
    }
}