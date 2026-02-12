package com.zero9platform.domain.gpp_comment.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gpp_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GppComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpp_id")
    private GroupPurchasePost groupPurchasePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public GppComment(GroupPurchasePost groupPurchasePost, User user, String content) {
        this.groupPurchasePost = groupPurchasePost;
        this.user = user;
        this.content = content;
    }

    /**
     * 댓글 수정
     */
    public void update(String content) {
        if (content != null) {
            this.content = content;
        }
    }
}