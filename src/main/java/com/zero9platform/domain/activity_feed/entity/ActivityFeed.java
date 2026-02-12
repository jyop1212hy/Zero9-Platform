package com.zero9platform.domain.activity_feed.entity;

import com.zero9platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "activity_feed",
        indexes = {
                @Index(name = "idx_user_updated", columnList = "user_id, updated_at DESC")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityFeed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 255)
    private String targetName;

    // 상품판매게시물ID, 공지사항 ID 등 모든 목적지 ID를 담는 필드
    @Column
    private Long targetId;

    // userId가 null이면 전체공개피드, 값이 있으면 해당하는 유저의 개인 알림피드
    @Column
    private Long userId;

    public ActivityFeed(String type, String targetName, Long targetId, Long userId) {
        this.type = type;
        this.targetName = targetName;
        this.targetId = targetId;
        this.userId = userId;
    }

    public void updateUpdatedAt(String targetName) {
        this.targetName = targetName;
    }
}