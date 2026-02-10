package com.zero9platform.domain.searchLog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "search_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String keyword;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SearchLog(String keyword,Long userId) {
        this.keyword = keyword;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
