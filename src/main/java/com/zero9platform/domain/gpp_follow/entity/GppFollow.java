package com.zero9platform.domain.gpp_follow.entity;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gpp_follows")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GppFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpp_id")
    private GroupPurchasePost groupPurchasePost;

    public GppFollow(User user, GroupPurchasePost groupPurchasePost) {
        this.user = user;
        this.groupPurchasePost = groupPurchasePost;
    }
}