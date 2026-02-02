package com.zero9platform.domain.searchLog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "search_contexts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_post_id", nullable = false)
    private Long productPostId;

    @Column(nullable = false, length = 255)
    private String keyword;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SearchContext(String keyword, Long productPostId, Long userId) {
        this.productPostId = productPostId;
        this.keyword = keyword;
        this.userId = userId;
    }
}