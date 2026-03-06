package com.zero9platform.domain.notification.entity;

import com.zero9platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
private Long userId;                    // 알림 받을 사용자

    @Column(nullable = false)
    private Long groupPurchasePostId;   // 어떤 공동구매 게시물 관련 알림인지

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private boolean isRead = false;

    public Notification(Long userId, Long groupPurchasePostId, String title, String content) {
        this.userId = userId;
        this.groupPurchasePostId = groupPurchasePostId;
        this.title = title;
        this.content = content;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
