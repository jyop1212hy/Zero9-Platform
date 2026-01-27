package com.zero9platform.domain.clickLog.entity;

import com.zero9platform.domain.product_post.entity.ProductPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "click_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClickLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_post_id")
    private ProductPost productPost;

    @Column(nullable = true)
    private String keyword;

    private LocalDateTime clickedAt;

    public ClickLog(ProductPost productPost, String keyword) {
        this.productPost = productPost;
        this.keyword = keyword;
        this.clickedAt = LocalDateTime.now();
    }
}