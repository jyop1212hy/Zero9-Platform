package com.zero9platform.domain.product_post_favorite.entity;

import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "product_posts_favorites", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPostFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_id", nullable = false)
    private ProductPost productPost;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    //찜 등록용 생성자 주입
    public ProductPostFavorite(User user, ProductPost productPost) {
        this.user = user;
        this.productPost = productPost;
    }
}