package com.zero9platform.domain.admin.entity;

import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "influencers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Influencer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String influencerSocialLink;

    @Column(nullable = false)
    private Boolean influencerApprovalStatus;

    @Column
    private LocalDateTime approvalAt;

    public Influencer(User user, String influencerSocialLink) {
        this.user = user;
        this.influencerSocialLink = influencerSocialLink;

        this.influencerApprovalStatus = false;
    }

    /**
     * 인플루언서 승인상태 변경
     */
    public void influencerApprove(Boolean approve) {
        this.influencerApprovalStatus = approve;
        this.approvalAt = LocalDateTime.now();
    }

    /**
     * 인플루언서 승인상태 확인
     */
    public boolean isApproved() {
        return influencerApprovalStatus;
    }
}
