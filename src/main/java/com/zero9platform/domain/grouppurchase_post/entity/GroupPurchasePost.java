package com.zero9platform.domain.grouppurchase_post.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "group_purchase_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPurchasePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private String image;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long price = 0L;

    @Column(nullable = false)
    private String linkUrl;

    @Column(nullable = false)
    private String category = Category.ETC.name();

    @Column(nullable = false)
    private String gppProgressStatus = GppProgressStatus.READY.name();

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private LocalDateTime deletedAt;

    public GroupPurchasePost(User user, String productName, String content, String image, Long price, String linkUrl, String category, String gppProgressStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.user = user;
        this.productName = productName;
        this.content = content;
        this.image = image;
        this.price = price;
        this.linkUrl = linkUrl;
        this.category = category;
        this.gppProgressStatus = gppProgressStatus;
        this.viewCount = 0L;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * gpp 게시물 수정
     */
    public void update(String productName, String content, String image, Long price, String linkUrl, String category, String gppProgressStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.productName = productName;
        this.content = content;
        this.image = image;
        this.price = price;
        this.linkUrl = linkUrl;
        this.category = category;
        this.gppProgressStatus = gppProgressStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * gpp 게시물 삭제
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 조회 수 증가 - 영속 엔티티 상태 변경, 영속성 컨텍스트를 거침
    // 트랜잭션 종료 시 flush
    // 대량 트래픽에 비효율적
//    public void increaseViewCount() {
//        this.viewCount++;
//    }

}
