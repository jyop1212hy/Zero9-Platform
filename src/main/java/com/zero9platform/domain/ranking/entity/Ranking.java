package com.zero9platform.domain.ranking.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "rankings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNo;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private String orderStatus;

    @Column
    private LocalDateTime canceledAt;

    public Ranking(String orderNo, Long totalAmount, String orderStatus, LocalDateTime canceledAt) {
        this.orderNo = orderNo;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.canceledAt = canceledAt;
    }
}
