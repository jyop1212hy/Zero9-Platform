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

    
    /**
     * gpp 생성자
     */
    //    public GroupPurchasePost(User user, String productName, String content, String image, Long price, String linkUrl, String category, String gppProgressStatus, LocalDateTime startDate, LocalDateTime endDate) {
    public GroupPurchasePost(User user, String productName, String content, String image, Long price, String linkUrl, String category, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime now) {
        this.user = user;
        this.productName = productName;
        this.content = content;
        this.image = image;
        this.price = price;
        this.linkUrl = linkUrl;
        this.category = category;
//        this.gppProgressStatus = GppProgressStatus.READY.name(); // 더이상 모집상태 입력을 받지 않고, 생성 시점에 연산한다
        this.viewCount = 0L;
        this.startDate = startDate;
        this.endDate = endDate;

        updateProgressStatus(now); // LocalDateTime.now() <- 엔티티 스스로 현재 시간을 판단해선 안되며, 외부에서 주입받는다
    }

    /**
     * gpp 게시물 수정
     */
//    public void update(String productName, String content, String image, Long price, String linkUrl, String category, String gppProgressStatus, LocalDateTime startDate, LocalDateTime endDate) {
    public void update(String productName, String content, String image, Long price, String linkUrl, String category, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime now) {
        this.productName = productName;
        this.content = content;
        this.image = image;
        this.price = price;
        this.linkUrl = linkUrl;
        this.category = category;
//        this.gppProgressStatus = gppProgressStatus; // 더이상 모집상태 입력을 받지 않고, 생성 시점에 연산한다
        this.startDate = startDate;
        this.endDate = endDate;

        // 순서 매우 중요, 바꾸지 말것
        // start/endDate 변경 이후 현재 시점 기준으로 상태 재계산
        updateProgressStatus(now); // 날짜 변경 시 상태 재계산
    }

    /**
     * gpp 게시물 삭제
     */
//    public void softDelete() {
//        this.deletedAt = LocalDateTime.now();
//    }
    // 현재시간을 외부에서 주입받도록 수정함
    public void softDelete(LocalDateTime now) {
        this.deletedAt = now;
    }

    /**
     * Enum -> value의 description 반환 (응답dto용)
     */
    public String getCategoryDescription() {
        return Category.valueOf(this.category).getDescription();
    }
    public String getProgressStatusDescription() {
        return GppProgressStatus.valueOf(this.gppProgressStatus).getDescription();
    }

    /**
     * 모집상태 전환 -> 해당 엔티티 클래스 안에서만 사용 가능 (protected 접근 제어)
     */
    protected void updateProgressStatus(LocalDateTime now) {
        // 이 메서드는 "생성 / 수정" 시점에만 사용
        // 모집상태 전환은 Scheduler + Bulk Update 쿼리 메서드로만 처리
        GppProgressStatus newStatus;

        if (now.isBefore(startDate)) {
            newStatus = GppProgressStatus.READY;
        } else if (now.isBefore(endDate)) {
            newStatus = GppProgressStatus.DOING;
        } else {
            newStatus = GppProgressStatus.END;
        }

        this.gppProgressStatus = newStatus.name();
    }

    // 조회 수 증가 - 영속 엔티티 상태 변경, 영속성 컨텍스트를 거침
    // 트랜잭션 종료 시 flush
    // 대량 트래픽에 비효율적
//    public void increaseViewCount() {
//        this.viewCount++;
//    }

}
