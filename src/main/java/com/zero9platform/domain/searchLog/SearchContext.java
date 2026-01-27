package com.zero9platform.domain.searchLog;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity(name = "search_Context")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productPostId;

    @Column(nullable = true)
    private String keyword;

    private LocalDateTime createdAt;

    public SearchContext(String keyword, Long productPostId) {
        this.keyword = keyword;
        this.productPostId = productPostId;
        this.createdAt = LocalDateTime.now();
    }
}